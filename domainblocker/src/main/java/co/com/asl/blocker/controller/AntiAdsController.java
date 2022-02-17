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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

  public void process(String hostsFile, Operation operation) throws IOException {
    Assert.notNull(hostsFile, "Hosts files required");
    Assert.notNull(operation, "Operation required");

    StopWatch stopWatch = new StopWatch();
    log.info("Creating file {}", hostsFile);
    stopWatch.start();

    Path hostsFilePath = Path.of(hostsFile);
    Files.deleteIfExists(hostsFilePath);

    List<String> fileLines = new ArrayList<>();
    fileLines.add(preludeLinesLoader.load());
    fileLines.add("");
    fileLines.add("# Generated hosts");
    fileLines.addAll(hostsLinesCreator.load(operation));
    Files.write(hostsFilePath, fileLines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

    stopWatch.stop();
    log.info("File {} was created in {} seconds", hostsFile,
        Long.valueOf(stopWatch.getTime() / 1000L));
  }
}
