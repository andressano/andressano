package co.com.asl.firewall.configuration.ufw;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
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
import co.com.asl.firewall.lines.ufw.UfwUserRuleLinesUtil;
import co.com.asl.firewall.resources.FileToLinesResourceLoader;
import lombok.extern.slf4j.Slf4j;

@Scope("prototype")
@Component
@Slf4j
public final class UFWConfigurator extends AbstractConfigurator {

  @Autowired
  private FileToLinesResourceLoader fileToLinesResourceLoader;
  @Autowired
  private BeanFactory beanFactory;
  @Autowired
  private ResourcePatternResolver resourcePatternResolver;
  @Autowired
  private Collection<IPListLoader> listLoaders;

  public UFWConfigurator(String profile, String outputFile) {
    super(profile, outputFile);
  }

  private Collection<String> loadRulesLines() {
    Collection<String> addressRulesLines = new ArrayList<>();
    for (FWOperation ufwOperation : FWOperation.values()) {
      Collection<String> addresses = listLoaders
          .stream()
          .flatMap(ll -> ll.load(FirewallType.UFW, getProfile(), ufwOperation))
          .collect(Collectors.toCollection(CIDRTransformableSet::new))
          .transform()
          .stream()
          .flatMap(a -> UfwUserRuleLinesUtil.execute(a, ufwOperation))
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
    lines.addAll(readFiles(StringUtils.join(getProfile(), File.separator, "startRules.txt")));
    lines.addAll(loadRulesLines());
    lines.addAll(readFiles(StringUtils.join(getProfile(), File.separator, "endRules.txt")));
    lines.addAll(List.of("", "### END RULES ###", ""));
    lines.addAll(readFiles("end.txt"));

    final Path userRulesPath = Path.of(getOutputFile());
    Files.write(userRulesPath, lines, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
        StandardOpenOption.TRUNCATE_EXISTING);
  }

  private Collection<String> readFiles(String file) throws IOException {
    Collection<String> lines = new ArrayList<>();
    lines.addAll(fileToLinesResourceLoader.load(resourcePatternResolver.getResources(
        String.format("classpath*:META-INF/firewall/ufw/%s", file))).collect(Collectors.toList()));
    return lines;
  }
}
