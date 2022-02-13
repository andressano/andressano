package co.com.asl.firewall.configuration;

import co.com.asl.firewall.command.AddUfwUserRuleLines;
import co.com.asl.firewall.entities.CIDRAddressV4;
import co.com.asl.firewall.file.FlatResourceLinesTransformer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
public final class UFWConfigurator extends AbstractConfigurator {

  @Autowired
  private FlatResourceLinesTransformer flatResourceLinesTransformer;

  @Autowired
  private BeanFactory beanFactory;

  private Collection<String> loadRulesLines() throws IOException {
    Collection<String> addressRulesLines = new ArrayList<>();
    MultiValuedMap<UFWOperation, CIDRAddressV4> cidrsByOperation = super.getCidrsByOperation();
    for (UFWOperation ufwOperation : UFWOperation.values()) {
      Collection<CIDRAddressV4> addresses = new TreeSet<>(cidrsByOperation.get(ufwOperation));
      if (!CollectionUtils.isEmpty(addresses)) {
        if (log.isInfoEnabled()) {
          log.info("There are {} CIDR addresses for operation {}", addresses.size(),
              ufwOperation);
        }
        addressRulesLines.addAll(
            addresses.stream().flatMap(a -> AddUfwUserRuleLines.execute(a, ufwOperation))
                .collect(Collectors.toList()));
      }
    }
    return addressRulesLines;
  }

  private void writeUserRules(final String userRules) throws IOException {
    final Path userRulesPath = Path.of(userRules);
    Files.deleteIfExists(userRulesPath);
    Files.write(userRulesPath, readFiles("start.txt"), StandardOpenOption.CREATE,
        StandardOpenOption.APPEND);
    Files.write(userRulesPath, List.of("### RULES ###"), StandardOpenOption.APPEND);
    Files.write(userRulesPath, readFiles("startRules.txt"), StandardOpenOption.APPEND);
    Files.write(userRulesPath, loadRulesLines(), StandardOpenOption.APPEND);
    Files.write(userRulesPath, readFiles("endRules.txt"), StandardOpenOption.APPEND);
    Files.write(userRulesPath, List.of("", "### END RULES ###", ""), StandardOpenOption.APPEND);
    Files.write(userRulesPath, readFiles("end.txt"), StandardOpenOption.APPEND);
  }

  private Collection<String> readFiles(String file) throws IOException {
    return Stream.concat(
        Arrays.stream(resourcePatternResolver.getResources(
            String.format("classpath*:META-INF/firewall/%s", file))),
        Arrays.stream(resourcePatternResolver.getResources(
            String.format("classpath*:META-INF/firewall/%s/**/%s", getSetting(), file)))
    ).flatMap(r -> flatResourceLinesTransformer.transform(r)).collect(Collectors.toList());
  }

  public void configure(String setting, String userRules) throws IOException {
    StopWatch stopWatch = new StopWatch();
    final boolean isInfoEnabled = log.isInfoEnabled();
    stopWatch.start();
    setSetting(setting);
    if (isInfoEnabled) {
      log.info("UFW configuration '{}' started", getSetting());
    }
    writeUserRules(userRules);
    stopWatch.stop();
    if (isInfoEnabled) {
      log.info("UFW configuration '{}' finished in {} seconds", getSetting(),
          stopWatch.getTotalTimeSeconds());
    }
  }
}
