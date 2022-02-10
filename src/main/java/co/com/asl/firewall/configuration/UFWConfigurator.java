package co.com.asl.firewall.configuration;

import co.com.asl.firewall.command.CommandClosure;
import co.com.asl.firewall.command.RuleUfwUserRulesTransformer;
import co.com.asl.firewall.entities.CIDRAddressV4;
import co.com.asl.firewall.file.FlatResourceLinesTransformer;
import co.com.asl.firewall.file.ResourceLinesTransformer;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public final class UFWConfigurator extends AbstractConfigurator {

    @Autowired
    private ResourceLinesTransformer resourceLinesTransformer;
    @Autowired
    private FlatResourceLinesTransformer flatResourceLinesTransformer;

    @Autowired
    private BeanFactory beanFactory;

    public UFWConfigurator() {
    }

    private Collection<String> loadRulesLines() throws IOException {
        Collection<String> addressRulesLines = new ArrayList<>();
        MultiValuedMap<UFWOperation, CIDRAddressV4> cidrsByOperation = super.getCidrsByOperation();
        for (UFWOperation ufwOperation : UFWOperation.values()) {
            RuleUfwUserRulesTransformer ruleUfwUserRulesTransformer = beanFactory.getBean(RuleUfwUserRulesTransformer.class, ufwOperation);
            Collection<CIDRAddressV4> addresses = new TreeSet<>(cidrsByOperation.get(ufwOperation));
            if (!CollectionUtils.isEmpty(addresses)) {
                if (log.isInfoEnabled())
                    log.info("There are {} CIDR addresses for operation {}", addresses.size(), ufwOperation);
                addressRulesLines.addAll(addresses.stream().flatMap(ruleUfwUserRulesTransformer::transform).collect(Collectors.toList()));
            }
        }
        return addressRulesLines;
    }

    private void writeUserRules(String userRules) throws IOException {
        FileWriter fileWriter = new FileWriter(userRules);
        final String LINE_SEPARATOR = System.getProperty("line.separator");

        IOUtils.writeLines(readFiles("start.txt"), LINE_SEPARATOR, fileWriter);
        IOUtils.writeLines(List.of("### RULES ###"), LINE_SEPARATOR, fileWriter);
        IOUtils.writeLines(readFiles("startRules.txt"), LINE_SEPARATOR, fileWriter);
        IOUtils.writeLines(loadRulesLines(), LINE_SEPARATOR, fileWriter);
        IOUtils.writeLines(readFiles("endRules.txt"), LINE_SEPARATOR, fileWriter);
        IOUtils.writeLines(List.of("", "### END RULES ###", ""), LINE_SEPARATOR, fileWriter);
        IOUtils.writeLines(readFiles("end.txt"), LINE_SEPARATOR, fileWriter);

        fileWriter.close();
    }

    private List<String> readFiles(String file) throws IOException {
        List<Resource> resources = new ArrayList<>();
        resources.addAll(Arrays.asList(resourcePatternResolver.getResources(String.format("classpath*:META-INF/firewall/%s", file))));
        resources.addAll(Arrays.asList(resourcePatternResolver.getResources(String.format("classpath*:META-INF/firewall/%s/**/%s", getSetting(), file))));
        return resources.stream().flatMap(flatResourceLinesTransformer::transform).collect(Collectors.toList());
    }

    private void runCommand(String file) throws IOException {
        List<Resource> resources = new ArrayList<>();
        resources.addAll(Arrays.asList(resourcePatternResolver.getResources(String.format("classpath*:META-INF/firewall/%s", file))));
        resources.addAll(Arrays.asList(resourcePatternResolver.getResources(String.format("classpath*:META-INF/firewall/%s/**/%s", getSetting(), file))));
        resources.stream().flatMap(resourceLinesTransformer::transform).forEach(l -> beanFactory.getBean(CommandClosure.class, l).execute(""));
    }


    public void configure(String setting, String userRules) throws IOException {
        StopWatch stopWatch = new StopWatch();
        final boolean isInfoEnabled = log.isInfoEnabled();
        stopWatch.start();
        setSetting(setting);
        if (isInfoEnabled) log.info("UFW configuration '{}' started", getSetting());
        //runCommand("startCommand.txt");
        writeUserRules(userRules);
        //runCommand("endCommand.txt");
        stopWatch.stop();
        if (isInfoEnabled)
            log.info("UFW configuration '{}' finished in {} seconds", getSetting(), stopWatch.getTotalTimeSeconds());
    }
}
