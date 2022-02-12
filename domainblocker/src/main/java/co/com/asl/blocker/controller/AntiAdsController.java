package co.com.asl.blocker.controller;

import co.com.asl.blocker.enums.Operation;
import co.com.asl.blocker.file.HostsFileTransformer;
import co.com.asl.blocker.file.URLToLinesTransformer;
import co.com.asl.blocker.file.line.LineConstants;
import co.com.asl.blocker.file.line.LinePredicates;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Slf4j
public class AntiAdsController {

  @Value("#{ blacklist }")
  private List<String> blacklist;
  @Autowired
  private ResourceLoader resourceLoader;
  @Autowired
  private HostsFileTransformer hostsFileTransformer;
  @Autowired
  private URLToLinesTransformer urlToLinesTransformer;

  public void process(String hostsFile, Operation operation) throws IOException {
    Assert.notNull(hostsFile);
    Assert.notNull(operation);

    StopWatch stopWatch = new StopWatch();
    log.info("Creating file {}", hostsFile);
    stopWatch.start();

    Path hostsFilePath = Path.of(hostsFile);
    List<String> fileLines = hostsFileTransformer.transform();
    fileLines.addAll(createPreludeLines());
    Files.deleteIfExists(hostsFilePath);
    Files.write(hostsFilePath, fileLines);
    fileLines.clear();

    Set<String> hostsLines = new TreeSet<>();
    if (Operation.CREATE_HOSTS_FILE.equals(operation)) {
      Path sitesPath = Path.of(
          resourceLoader.getResource("classpath:/META-INF/antiads/sites.txt").getURI());

      Set<String> hosts =
          Files.lines(sitesPath)
              .filter(LinePredicates.isNotComment())
              .map(urlToLinesTransformer::transform)
              .flatMap(Set::stream)
              .collect(Collectors.toSet());

      hostsLines.addAll(hosts);
      hostsLines.addAll(blacklist);
    }
    hostsLines.addAll(blacklist);
    fileLines.addAll(
        hostsLines.stream().map(l -> String.format("%s\t%s", LineConstants.ROUTE_IP, l))
            .collect(Collectors.toList()));
    Files.write(hostsFilePath, fileLines, StandardOpenOption.APPEND);
    stopWatch.stop();
    log.info("File {} was created in {} seconds", hostsFile,
        Long.valueOf(stopWatch.getTime() / 1000L));
  }

  private Set<String> createPreludeLines() {
    return new HashSet<>(Arrays.asList("", "# Generated hosts"));
  }
}
