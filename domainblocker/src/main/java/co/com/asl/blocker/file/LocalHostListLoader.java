package co.com.asl.blocker.file;

import co.com.asl.blocker.file.line.LineConstants;
import io.vavr.control.Try;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourcePatternResolver;

@Slf4j
public abstract class LocalHostListLoader implements HostListLoader {

  @Autowired
  private ResourcePatternResolver resourcePatternResolver;

  protected Stream<String> loadLocalHostsLines(String classpath) throws IOException {
    return Arrays.stream(resourcePatternResolver.getResources(classpath))
        .map(r -> Try.of(r::getInputStream)
            .onFailure(e -> log.error("Error reading resource ".concat(r.getFilename()), e))
            .getOrNull())
        .filter(Objects::nonNull)
        .flatMap(LinesLoader::loadLines);
  }
}
