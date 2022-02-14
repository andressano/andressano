package co.com.asl.blocker.controller;

import co.com.asl.blocker.enums.Operation;
import co.com.asl.blocker.file.URLToLinesTransformer;
import co.com.asl.blocker.file.line.LineConstants;
import co.com.asl.blocker.file.line.LinePredicates;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Slf4j
public class AntiAdsController {

  @Autowired
  private ResourceLoader resourceLoader;
  @Autowired
  private URLToLinesTransformer urlToLinesTransformer;
  @Autowired
  private BeanFactory beanFactory;

  @Autowired
  @Qualifier("preludeLines")
  private String preludeLines;
  @Autowired
  @Qualifier("blacklist")
  private Collection<String> blacklist;
  @Autowired
  @Qualifier("whitelist")
  private Collection<String> whitelist;

  private boolean filterWhiteList(String host) {
    return StringUtils.isNotBlank(host) && whitelist.stream().noneMatch(host::endsWith);
  }

  public void process(String hostsFile, Operation operation) throws IOException {
    Assert.notNull(hostsFile, "Hosts files required");
    Assert.notNull(operation, "Operation required");

    StopWatch stopWatch = new StopWatch();
    log.info("Creating file {}", hostsFile);
    stopWatch.start();

    Path hostsFilePath = Path.of(hostsFile);
    Files.deleteIfExists(hostsFilePath);

    List<String> fileLines = new ArrayList<>();
    fileLines.add(preludeLines);
    fileLines.addAll(createPreludeLines());

    Set<String> hostsLines = new TreeSet<>();
    hostsLines.addAll(blacklist);

    if (Operation.CREATE_HOSTS_FILE.equals(operation)) {
      Path sitesPath = Path.of(
          resourceLoader.getResource("classpath:/META-INF/antiads/sites.txt").getURI());

      try (Stream<String> stream = Files.lines(sitesPath)) {
        Set<String> hosts = stream
            .filter(LinePredicates.isNotComment())
            .map(urlToLinesTransformer::transform)
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
        hostsLines.addAll(hosts);
      }
    }
    fileLines.addAll(
        hostsLines.stream()
            .map(l -> String.format("%s\t%s", LineConstants.ROUTE_IP, l))
            .filter(this::filterWhiteList)
            .collect(Collectors.toList()));
    Files.write(hostsFilePath, fileLines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

    stopWatch.stop();
    log.info("File {} was created in {} seconds", hostsFile,
        Long.valueOf(stopWatch.getTime() / 1000L));
  }

  private List<String> createPreludeLines() {
    return List.of("", "# Generated hosts");
  }
}
