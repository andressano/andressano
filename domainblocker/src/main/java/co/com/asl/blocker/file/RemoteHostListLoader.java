package co.com.asl.blocker.file;

import co.com.asl.blocker.file.line.LineFunctions;
import io.vavr.control.Try;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourcePatternResolver;

@Slf4j
public abstract class RemoteHostListLoader implements HostListLoader {

  @Autowired
  protected ResourcePatternResolver resourcePatternResolver;

  protected Stream<String> loadRemoteHostsLines(String urlListPath) throws IOException {
    return
        Files.lines(Path.of(resourcePatternResolver.getResource(urlListPath).getURI()))
            .map(LineFunctions::removeComments)
            .filter(StringUtils::isNotBlank)
            .map(url -> Try.of(() -> new URL(url).openStream())
                .onFailure(e -> log.error("Error reading url ".concat(url), e))
                .getOrNull())
            .filter(Objects::nonNull)
            .flatMap(HostListLoader::loadLines);
  }
}
