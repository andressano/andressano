package co.com.asl.firewall.configuration;

import co.com.asl.firewall.entities.ASNumber;
import co.com.asl.firewall.entities.CIDRAddressV4;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.apache.commons.collections4.MultiValuedMap;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
public final class FileConfigurator extends AbstractConfigurator {

  private void writeIPs(String outputFile) throws IOException {
    MultiValuedMap<UFWOperation, ASNumber> asnsByOperation = super.getASNsByOperation();
    Path outputFilePath = Paths.get(outputFile);
    Files.write(outputFilePath, new byte[]{}, StandardOpenOption.CREATE_NEW);

    for (UFWOperation ufwOperation : UFWOperation.values()) {
      Files.write(outputFilePath, List.of(ufwOperation.name().concat(":\r\n")),
          StandardOpenOption.APPEND);
      for (ASNumber asNumber : new TreeSet<>(asnsByOperation.get(ufwOperation))) {
        if (!asNumber.isEmpty()) {
          Files.write(outputFilePath, List.of(asNumber.toString().concat(":\r\n")),
              StandardOpenOption.APPEND);
          Files.write(outputFilePath,
              asNumber
                  .stream()
                  .map(CIDRAddressV4::toString)
                  .collect(Collectors.toList()),
              StandardOpenOption.APPEND);
        }
      }
    }
  }

  public void configure(String configType, String filename) throws IOException {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    setSetting(configType);
    this.log.info("UFW configuration '{}' started", getSetting());
    writeIPs(filename);
    stopWatch.stop();
    this.log.info("UFW configuration '{}' finished in {} seconds", getSetting(),
        stopWatch.getTotalTimeSeconds());
  }
}
