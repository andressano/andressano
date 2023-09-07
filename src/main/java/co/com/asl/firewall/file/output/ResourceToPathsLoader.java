
package co.com.asl.firewall.file.output;

import co.com.asl.firewall.configuration.FWOperation;
import co.com.asl.firewall.configuration.FirewallType;
import co.com.asl.firewall.configuration.InputFileType;
import co.com.asl.firewall.resources.FileToLinesResourceLoader;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ResourceToPathsLoader {

  private final FileToLinesResourceLoader fileToLinesResourceLoader;

  @Autowired
  private ResourceToPathsLoader(
      FileToLinesResourceLoader fileToLinesResourceLoader) {
    this.fileToLinesResourceLoader = fileToLinesResourceLoader;
  }

  public static final String REGEX_PATTERN = "^(\\/[\\w-]+)+(.([a-zA-Z]+)?)$";

  private static final Predicate<String> PREDICATE = Pattern.compile(REGEX_PATTERN)
          .asMatchPredicate();

  public Stream<String> load(FirewallType firewallType, String setting, FWOperation ufwOperation) {
    return fileToLinesResourceLoader
        .load(InputFileType.PATHS, firewallType, setting, ufwOperation)
        .map(l -> l.replaceFirst("#(.)*", ""))
        .map(l -> l.replaceAll("\\s+", ""))
        .filter(StringUtils::isNotBlank)
        .filter(PREDICATE);
  }
}
