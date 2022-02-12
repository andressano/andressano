package co.com.asl.firewall.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class ResourceLinesTransformer {

  private final Logger log = LoggerFactory.getLogger(getClass());

  public Collection<String> transform(Resource resource) {
    try (Stream<String> stream = Files.lines(Path.of(resource.getURI()))) {
      return stream
          .map(l -> l.replaceFirst("#(.)*", ""))
          .map(String::trim)
          .filter(s -> !StringUtils.isBlank(s))
          .collect(Collectors.toList());
    } catch (IOException e) {
      this.log.error(e.getLocalizedMessage(), e);
    }
    return Collections.emptyList();
  }
}
