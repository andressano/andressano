package co.com.asl.firewall.configuration;

import co.com.asl.firewall.command.AddUfwUserRuleLines;
import co.com.asl.firewall.entities.transform.CIDRTransformableSet;
import co.com.asl.firewall.file.IPListLoader;
import co.com.asl.firewall.resources.FileToLinesResourceLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
public final class UFWConfigurator extends AbstractConfigurator {

  @Autowired
  private FileToLinesResourceLoader fileToLinesResourceLoader;
  @Autowired
  private BeanFactory beanFactory;
  @Autowired
  private Collection<IPListLoader> listLoaders;

  private Collection<String> loadRulesLines() throws IOException {
    Collection<String> addressRulesLines = new ArrayList<>();
    for (UFWOperation ufwOperation : UFWOperation.values()) {
      Collection<String> addresses = listLoaders
          .stream()
          .flatMap(ll -> ll.load(this.getSetting(), ufwOperation))
          .collect(Collectors.toCollection(CIDRTransformableSet::new))
          .transform()
          .stream()
          .flatMap(a -> AddUfwUserRuleLines.execute(a, ufwOperation))
          .collect(Collectors.toList());
      if (CollectionUtils.isEmpty(addresses)) {
        continue;
      }
      addressRulesLines.addAll(addresses);
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
    Collection<String> lines = new ArrayList<>();
    lines.addAll(fileToLinesResourceLoader.load(resourcePatternResolver.getResources(
        String.format("classpath*:META-INF/firewall/%s", file))).collect(Collectors.toList()));
    lines.addAll(fileToLinesResourceLoader.load(resourcePatternResolver.getResources(
        String.format("classpath*:META-INF/firewall/%s/**/%s", getSetting(), file))).collect(
        Collectors.toList()));
    return lines;
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
