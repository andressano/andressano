package co.com.asl.firewall.file.output.ip;

import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.com.asl.firewall.configuration.FWOperation;
import co.com.asl.firewall.configuration.FirewallType;
import co.com.asl.firewall.configuration.InputFileType;
import co.com.asl.firewall.entities.CIDRAddressV4;
import co.com.asl.firewall.resources.FileToLinesResourceLoader;

@Component
public class ResourceToIPListLoader implements IPListLoader {

  @Autowired
  private FileToLinesResourceLoader fileToLinesResourceLoader;

  @Override
  public Stream<CIDRAddressV4> load(FirewallType firewallType, String setting, FWOperation ufwOperation) {
    return fileToLinesResourceLoader
        .load(InputFileType.IP_FILETYPE, firewallType, setting, ufwOperation)
        .map(l -> l.replaceFirst("#(.)*", ""))
        .map(l -> l.replaceAll("\\s+", ""))
        .filter(StringUtils::isNotBlank)
        .filter(CIDRAddressV4.PREDICATE)
        .map(CIDRAddressV4::new);
  }
}
