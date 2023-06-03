package co.com.asl.firewall.configuration.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import co.com.asl.firewall.configuration.AbstractConfigurator;
import co.com.asl.firewall.configuration.FWOperation;
import co.com.asl.firewall.configuration.FirewallType;
import co.com.asl.firewall.entities.CIDRAddressV4;
import co.com.asl.firewall.entities.transform.CIDRTransformableSet;
import co.com.asl.firewall.file.output.ip.IPListLoader;

@Scope("prototype")
@Component
public final class FileRuleGroupConfigurator extends AbstractConfigurator {

  @Autowired
  private Collection<IPListLoader> listLoaders;

  public FileRuleGroupConfigurator(String profile, String outputFile) {
    super(profile, outputFile);
  }
  
  private List<String> joinAddreses(List<String> addresses, final int size) {
      
      List<String> result = new ArrayList<>();
 	  int group = 1;
	  StringBuilder sb = new StringBuilder();
	  for (int i = 0; i < addresses.size(); i++) {
	    final String ip = addresses.get(i);
	    final int proposedLength = sb.length() + ip.length();
	    if (proposedLength <= size -1) {
	      sb.append(ip);
	    }
	    if (proposedLength + 1 <= size) {
	      sb.append(",");
	    }
	    if (proposedLength > size || i == addresses.size() - 1) {
	      result.add(String.format("GROUP %d:", group++));
	      if (sb.charAt(sb.length() - 1) == ',') {
	        sb.deleteCharAt(sb.length() - 1);
	      }
	      result.add(sb.toString());
	      sb.delete(0, sb.length());
	    }
	  }
	return result;
	  
  }
  

  private Collection<String> loadRulesLines() {
    Collection<String> addressRulesLines = new ArrayList<>();
    for (FWOperation ufwOperation : FWOperation.values()) {
      ArrayList<String> addresses = listLoaders
          .stream()
          .flatMap(ll -> ll.load(FirewallType.UFW, getProfile(), ufwOperation))
          .collect(Collectors.toCollection(CIDRTransformableSet::new))
          .transform()
          .stream()
          .map(CIDRAddressV4::toString)
          .collect(Collectors.toCollection(ArrayList::new));

      if (CollectionUtils.isEmpty(addresses)) {
        continue;
      }

      addressRulesLines.add(ufwOperation.name().concat(":"));
      addressRulesLines.addAll(joinAddreses(addresses, 32767));
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
