package co.com.asl.blocker.controller;

import co.com.asl.blocker.enums.Operation;
import co.com.asl.blocker.file.HostsLinesCreator;
import co.com.asl.blocker.file.PreludeLinesLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Slf4j
public class AntiAdsController {

  @Autowired
  private ResourceLoader resourceLoader;
  @Autowired
  private PreludeLinesLoader preludeLinesLoader;
  @Autowired
  private HostsLinesCreator hostsLinesCreator;

  protected void createHostsFile(String hostsFile, Operation operation) throws IOException {
    Path hostsFilePath = Path.of(hostsFile);
    Files.deleteIfExists(hostsFilePath);

    List<String> fileLines = new ArrayList<>();
    fileLines.addAll(preludeLinesLoader.loadLines().collect(Collectors.toList()));
    fileLines.add("");
    fileLines.add("# Generated hosts");
    fileLines.addAll(hostsLinesCreator.create(operation));
    Files.write(hostsFilePath, fileLines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
  }

  public void process(String hostsFile, Operation operation) throws IOException {
    Assert.notNull(hostsFile, "Hosts files required");
    Assert.notNull(operation, "Operation required");

    StopWatch stopWatch = new StopWatch();
    log.info("Creating file {}", hostsFile);
    stopWatch.start();

    createHostsFile(hostsFile, operation);

    stopWatch.stop();
    log.info("File {} was created in {} seconds", hostsFile,
        Long.valueOf(stopWatch.getTime() / 1000L));
  }
}
