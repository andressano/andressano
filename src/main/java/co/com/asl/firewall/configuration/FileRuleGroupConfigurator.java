package co.com.asl.firewall.configuration;

import co.com.asl.firewall.entities.CIDRAddressV4;
import co.com.asl.firewall.entities.transform.CIDRTransformableSet;
import co.com.asl.firewall.file.output.ip.IPListLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Scope("prototype")
@Component
@Slf4j
public final class FileRuleGroupConfigurator extends AbstractConfigurator {

  private final int IPS_GROUP = 500;
  @Autowired
  private Collection<IPListLoader> listLoaders;

  public FileRuleGroupConfigurator(String profile, String outputFile) {
    super(profile, outputFile);
  }

  private Collection<String> loadRulesLines() {
    Collection<String> addressRulesLines = new ArrayList<>();
    int group = 1;
    for (UFWOperation ufwOperation : UFWOperation.values()) {
      ArrayList<String> addresses = listLoaders
          .stream()
          .flatMap(ll -> ll.load(getProfile(), ufwOperation))
          .collect(Collectors.toCollection(CIDRTransformableSet::new))
          .transform()
          .stream()
          .map(CIDRAddressV4::toString)
          .collect(Collectors.toCollection(ArrayList::new));

      if (CollectionUtils.isEmpty(addresses)) {
        continue;
      }

      addressRulesLines.add(ufwOperation.name().concat(":"));
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < addresses.size(); i++) {
        final String ip = addresses.get(i);
        final int proposedLength = sb.length() + ip.length();
        if (proposedLength <= 32766) {
          sb.append(ip);
        }
        if (proposedLength + 1 <= 32767) {
          sb.append(",");
        }
        if (proposedLength > 32767 || i == addresses.size() - 1) {
          addressRulesLines.add(String.format("GROUP %d:", group++));
          if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
          }
          addressRulesLines.add(sb.toString());
          sb.delete(0, sb.length());
        }
      }
    }
    return addressRulesLines;
  }

  @Override
  protected void writeFile() throws IOException {
    final Path userRulesPath = Path.of(getOutputFile());
    Files.write(userRulesPath, loadRulesLines(), StandardOpenOption.CREATE,
        StandardOpenOption.WRITE,
        StandardOpenOption.TRUNCATE_EXISTING);
  }
}
