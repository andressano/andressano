package co.com.asl.firewall.file.output.ip;

import co.com.asl.firewall.configuration.InputFileType;
import co.com.asl.firewall.configuration.UFWOperation;
import co.com.asl.firewall.entities.CIDRAddressV4;
import co.com.asl.firewall.resources.FileToLinesResourceLoader;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResourceToIPListLoader implements IPListLoader {

  @Autowired
  private FileToLinesResourceLoader fileToLinesResourceLoader;

  @Override
  public Stream<CIDRAddressV4> load(String string, UFWOperation ufwOperation) {
    return fileToLinesResourceLoader
        .load(InputFileType.IP_FILETYPE, string, ufwOperation)
        .map(l -> l.replaceFirst("#(.)*", ""))
        .map(l -> l.replaceAll("\\s+", ""))
        .filter(StringUtils::isNotBlank)
        .filter(CIDRAddressV4.PREDICATE)
        .map(CIDRAddressV4::new);
  }
}
