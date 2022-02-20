package co.com.asl.firewall.configuration;

import co.com.asl.firewall.entities.ASNumber;
import co.com.asl.firewall.entities.transform.CIDRTransformableSet;
import co.com.asl.firewall.file.ASNListLoader;
import co.com.asl.firewall.file.ResourceToASNListLoader;
import co.com.asl.firewall.file.ResourceToIPListLoader;
import co.com.asl.firewall.lines.file.AddFileLines;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

@Slf4j
@Component
public final class FileConfigurator {

  @Setter
  @Getter
  private String setting;
  @Autowired
  private ResourceToASNListLoader resourceToASNListLoader;
  @Autowired
  private ASNListLoader asnListLoader;
  @Autowired
  private ResourceToIPListLoader resourceToIPListLoader;
  @Autowired
  private AddFileLines addFileLines;

  private void writeIPs(String outputFile) throws IOException {
    Collection<String> addressRulesLines = new ArrayList<>();
    for (UFWOperation ufwOperation : UFWOperation.values()) {
      Collection<String> addresses = new ArrayList<>();
      addresses.addAll(addFileLines.createLines(
          resourceToIPListLoader.load(this.getSetting(), ufwOperation)
              .collect(Collectors.toCollection(() -> new CIDRTransformableSet("Others")))
              .transform()
      ).collect(Collectors.toList()));

      addresses.addAll(
          asnListLoader.load(resourceToASNListLoader.load(this.getSetting(), ufwOperation))
              .map(ASNumber::transform)
              .sorted()
              .flatMap(addFileLines::createLines)
              .collect(Collectors.toList()));

      if (!CollectionUtils.isEmpty(addresses)) {
        addressRulesLines.addAll(List.of("", ufwOperation.name().toUpperCase() + ":"));
      }
      addressRulesLines.addAll(addresses);
    }
    final Path userRulesPath = Path.of(outputFile);
    Files.deleteIfExists(userRulesPath);
    Files.write(userRulesPath, addressRulesLines, StandardOpenOption.CREATE,
        StandardOpenOption.APPEND);
  }

  public void configure(String configType, String filename) throws IOException {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    setSetting(configType);
    FileConfigurator.log.info("UFW configuration '{}' started", getSetting());
    writeIPs(filename);
    stopWatch.stop();
    FileConfigurator.log.info("UFW configuration '{}' finished in {} seconds", getSetting(),
        stopWatch.getTotalTimeSeconds());
  }
}
