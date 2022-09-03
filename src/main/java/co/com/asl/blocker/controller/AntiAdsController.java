package co.com.asl.blocker.controller;

import co.com.asl.blocker.enums.Operation;
import co.com.asl.blocker.line.generation.LinesCreator;
import io.vavr.control.Try;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
  private Collection<LinesCreator> linesCreators;

  protected void createHostsFile(String hostsFile, Operation operation) throws IOException {
    Path hostsFilePath = Path.of(hostsFile);
    Files.deleteIfExists(hostsFilePath);

    List<String> fileLines = linesCreators.stream()
        .filter(lc -> lc.isOperationAllowed(operation))
        .sorted(Comparator.comparing(LinesCreator::priority))
        .flatMap(lc -> Try.of(lc::create).getOrElse(Stream.empty()))
        .collect(Collectors.toList());

    Files.write(hostsFilePath, fileLines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
  }

  public void process(String hostsFile, Operation operation) throws IOException {
    Assert.notNull(hostsFile, "Hosts files required");
    Assert.notNull(operation, "Operation required");

    log.info("Creating file {}", hostsFile);
    createHostsFile(hostsFile, operation);
  }
}
