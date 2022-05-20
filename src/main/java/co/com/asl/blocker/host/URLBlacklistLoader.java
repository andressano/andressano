package co.com.asl.blocker.host;

import co.com.asl.blocker.line.LineFunctions;
import co.com.asl.blocker.line.reader.ResourceLinesReader;
import co.com.asl.blocker.line.reader.URLLinesReader;
import io.vavr.control.Try;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class URLBlacklistLoader implements Blacklist {

  @Autowired
  protected ResourcePatternResolver resourcePatternResolver;

  @Autowired
  private ResourceLinesReader resourceLinesReader;
  @Autowired
  private URLLinesReader urlLinesReader;

  public Collection<URL> loadURLs() throws IOException {
    return resourceLinesReader.loadLines(
            Arrays.asList(resourcePatternResolver.getResources("classpath:/META-INF/sites.txt")))
        .map(LineFunctions::removeComments)
        .map(StringUtils::trim)
        .filter(StringUtils::isNotBlank)
        .map(url -> Try.of(() -> new URL(url))
            .onFailure(e -> log.error("Error reading url ".concat(url), e))
            .getOrNull())
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  @Override
  public Stream<String> loadLines() throws IOException {
    return urlLinesReader.loadLines(loadURLs())
        .map(LineFunctions::removeComments)
        .map(StringUtils::trimToEmpty)
        .filter(StringUtils::isNotBlank)
        .map(LineFunctions::removeIp)
        .map(StringUtils::trimToEmpty)
        .filter(StringUtils::isNotBlank)
        .filter(LineFunctions::isValidLine);
  }
}
