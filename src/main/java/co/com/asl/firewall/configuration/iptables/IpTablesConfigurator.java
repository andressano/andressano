package co.com.asl.firewall.configuration.iptables;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import co.com.asl.firewall.configuration.AbstractConfigurator;
import co.com.asl.firewall.configuration.FWOperation;
import co.com.asl.firewall.configuration.FirewallType;
import co.com.asl.firewall.entities.transform.CIDRTransformableSet;
import co.com.asl.firewall.file.output.ip.IPListLoader;
import co.com.asl.firewall.lines.iptables.IpTablesInputUserRuleLinesUtil;
import co.com.asl.firewall.lines.iptables.IpTablesOutputUserRuleLinesUtil;
import co.com.asl.firewall.resources.FileToLinesResourceLoader;
import lombok.extern.slf4j.Slf4j;

@Scope("prototype")
@Component
@Slf4j
public final class IpTablesConfigurator extends AbstractConfigurator {

  @Autowired
  private FileToLinesResourceLoader fileToLinesResourceLoader;
  @Autowired
  private BeanFactory beanFactory;
  @Autowired
  private ResourcePatternResolver resourcePatternResolver;
  @Autowired
  private Collection<IPListLoader> listLoaders;

  public IpTablesConfigurator(String profile, String outputFile) {
    super(profile, outputFile);
  }

  private Collection<String> loadRulesLines() {
    Collection<String> addressRulesLines = new ArrayList<>();
    for (FWOperation ufwOperation : FWOperation.values()) {
      Collection<String> addresses = listLoaders
          .stream()
          .flatMap(ll -> ll.load(FirewallType.IPTABLES, getProfile(), ufwOperation))
          .collect(Collectors.toCollection(CIDRTransformableSet::new))
          .transform()
          .stream()
          .flatMap(a -> IpTablesInputUserRuleLinesUtil.execute(a, ufwOperation))
          .collect(Collectors.toList());
      addressRulesLines.addAll(addresses);
    }

    for (FWOperation ufwOperation : FWOperation.values()) {
      Collection<String> addresses = listLoaders
          .stream()
          .flatMap(ll -> ll.load(FirewallType.IPTABLES, getProfile(), ufwOperation))
          .collect(Collectors.toCollection(CIDRTransformableSet::new))
          .transform()
          .stream()
          .flatMap(a -> IpTablesOutputUserRuleLinesUtil.execute(a, ufwOperation))
          .collect(Collectors.toList());
      addressRulesLines.addAll(addresses);
    }
    return addressRulesLines;
  }

  @Override
  protected void writeFile() throws IOException {
    Collection<String> lines = new ArrayList<>();
    lines.addAll(readFiles("start.txt"));
    lines.addAll(List.of("### RULES ###"));
    lines.addAll(readFiles("endRules.txt"));
    lines.addAll(loadRulesLines());
    lines.addAll(readFiles("endRules.txt"));
    lines.addAll(List.of("", "### END RULES ###", ""));
    lines.addAll(readFiles("end.txt"));

    final Path userRulesPath = Path.of(getOutputFile());
    Files.write(userRulesPath, lines, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
        StandardOpenOption.TRUNCATE_EXISTING);
  }

  private Collection<String> readFiles(String file) throws IOException {
    Collection<String> lines = new ArrayList<>();
    lines.addAll(fileToLinesResourceLoader.load(resourcePatternResolver.getResources(
        String.format("classpath*:META-INF/firewall/iptables/%s", file))).collect(Collectors.toList()));
    lines.addAll(fileToLinesResourceLoader.load(resourcePatternResolver.getResources(
        String.format("classpath*:META-INF/firewall/iptables/%s/**/%s", getProfile(), file))).collect(
        Collectors.toList()));
    return lines;
  }
}
