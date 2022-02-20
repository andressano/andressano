package co.com.asl.firewall.file;

import co.com.asl.firewall.configuration.UFWOperation;
import co.com.asl.firewall.entities.ASNumber;
import co.com.asl.firewall.resources.FileToLinesResourceLoader;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResourceToASNListLoader {

  @Autowired
  private FileToLinesResourceLoader fileToLinesResourceLoader;

  public Stream<String> load(String setting, UFWOperation ufwOperation) {
    return fileToLinesResourceLoader
        .load(FileType.ASN_FILETYPE, setting, ufwOperation)
        .map(l -> l.replaceFirst("#(.)*", ""))
        .map(l -> l.replaceAll("\\s+", ""))
        .filter(StringUtils::isNotBlank)
        .filter(ASNumber.PREDICATE)
        .distinct();
  }
}
