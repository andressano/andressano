package co.com.asl.firewall.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class IPListResourceTransformer {

  private final Logger log = LoggerFactory.getLogger(getClass());

  public Stream<String> transform(Resource resource) {
    try {
      return Files.lines(Path.of(resource.getURI()))
          .map(l -> l.replaceFirst("#(.)*", ""))
          .map(String::trim)
          .filter(StringUtils::isNotBlank);
    } catch (IOException e) {
      this.log.error(e.getLocalizedMessage(), e);
    }
    return Stream.empty();
  }
}
