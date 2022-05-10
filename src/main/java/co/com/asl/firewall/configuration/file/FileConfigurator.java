package co.com.asl.firewall.configuration.file;

import co.com.asl.firewall.configuration.AbstractConfigurator;
import co.com.asl.firewall.configuration.ufw.UFWOperation;
import co.com.asl.firewall.file.output.asn.ASNLoader;
import co.com.asl.firewall.lines.file.FileLinesUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Scope("prototype")
@Slf4j
@Component
public final class FileConfigurator extends AbstractConfigurator {

  @Autowired
  private Collection<ASNLoader> asnLoaders;

  public FileConfigurator(String profile, String outputFile) {
    super(profile, outputFile);
  }

  @Override
  protected void writeFile() throws IOException {
    Collection<String> lines = new ArrayList<>();
    for (UFWOperation ufwOperation : UFWOperation.values()) {
      Collection<String> addresses = new ArrayList<>();
      addresses.addAll(
          asnLoaders.stream().flatMap(al -> al.load(getProfile(), ufwOperation))
              .sorted()
              .flatMap(FileLinesUtil::createLines)
              .collect(Collectors.toList()));

      if (!CollectionUtils.isEmpty(addresses)) {
        lines.addAll(List.of("", ufwOperation.name().toUpperCase() + ":"));
        lines.addAll(addresses);
      }
    }
    final Path userRulesPath = Path.of(outputFile);
    Files.write(userRulesPath, lines, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
        StandardOpenOption.TRUNCATE_EXISTING);
  }
}
