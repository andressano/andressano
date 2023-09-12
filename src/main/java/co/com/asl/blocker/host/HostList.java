package co.com.asl.blocker.host;

import co.com.asl.blocker.line.LineFunctions;
import co.com.asl.blocker.line.reader.ResourceLinesReader;
import co.com.asl.blocker.line.reader.URLLinesReader;
import io.vavr.control.Try;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.TreeSet;

@Slf4j
@Component
public class HostList extends TreeSet<String> {

  @Autowired
  protected ResourcePatternResolver resourcePatternResolver;

  @Autowired
  private ResourceLinesReader resourceLinesReader;
  @Autowired
  private URLLinesReader urlLinesReader;

  public Stream<String> loadURLLines() throws IOException {
    return Arrays.stream(resourcePatternResolver.getResources("classpath:/META-INF/sites.txt"))
        .flatMap(resourceLinesReader::loadLines)
        .map(LineFunctions::removeComments)
        .map(StringUtils::trim)
        .filter(StringUtils::isNotBlank)
        .map(url -> Try.of(() -> new URL(url))
            .onFailure(e -> log.error("Error reading url ".concat(url), e)).getOrNull())
        .flatMap(urlLinesReader::loadLines);
  }

  public Stream<String> loadDenyLines() throws IOException {
    return Arrays.stream(resourcePatternResolver
            .getResources("classpath:/META-INF/deny-list/*.txt"))
        .flatMap(resourceLinesReader::loadLines);
  }

  @PostConstruct
  public void loadLines() throws IOException {
    Stream.concat(loadURLLines(), loadDenyLines())
        .map(LineFunctions::removeComments)
        .map(StringUtils::trimToEmpty)
        .filter(StringUtils::isNotBlank)
        .map(LineFunctions::removeIp)
        .map(StringUtils::trimToEmpty)
        .filter(StringUtils::isNotBlank)
        .filter(LineFunctions::isValidHostnameLine)
        .forEach(this::add);
  }
}
