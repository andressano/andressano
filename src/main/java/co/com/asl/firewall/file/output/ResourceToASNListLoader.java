package co.com.asl.firewall.file.output;

import co.com.asl.firewall.configuration.FWOperation;
import co.com.asl.firewall.configuration.InputFileType;
import co.com.asl.firewall.entities.ASNumber;
import co.com.asl.firewall.resources.FileToLinesResourceLoader;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResourceToASNListLoader {

  private final FileToLinesResourceLoader fileToLinesResourceLoader;

  @Autowired
  private ResourceToASNListLoader(
      FileToLinesResourceLoader fileToLinesResourceLoader) {
    this.fileToLinesResourceLoader = fileToLinesResourceLoader;
  }

  public Stream<Integer> load(FWOperation ufwOperation) {
    return fileToLinesResourceLoader
        .load(InputFileType.ASN_FILETYPE, ufwOperation)
        .map(l -> l.replaceFirst("#(.)*", ""))
        .map(l -> l.replaceAll("\\s+", ""))
        .filter(StringUtils::isNotBlank)
        .filter(ASNumber.PREDICATE)
        .map(Integer::parseUnsignedInt)
        .distinct();
  }
}
