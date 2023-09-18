package co.com.asl.blocker.host;

import co.com.asl.blocker.config.URLFactory;
import co.com.asl.blocker.line.LineFunctions;
import co.com.asl.blocker.line.reader.ResourceLinesReader;
import co.com.asl.blocker.line.reader.URLLinesReader;
import io.vavr.control.Try;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EqualsAndHashCode(callSuper = true)
public class HostList extends TreeSet<String> {

  @Autowired
  protected transient ResourcePatternResolver resourcePatternResolver;

  @Autowired
  private ResourceLinesReader resourceLinesReader;
  @Autowired
  private URLLinesReader urlLinesReader;

  @Autowired
  @Qualifier("sitesClasspath")
  private String sitesClasspath;

  @Autowired
  @Qualifier("denyListClasspath")
  private String denyListClasspath;

  @Autowired
  private URLFactory urlFactory;

  public Stream<String> loadURLLines() throws IOException {
    return Arrays.stream(resourcePatternResolver
            .getResources(sitesClasspath))
        .flatMap(resourceLinesReader::loadLines)
        .map(LineFunctions::removeComments)
        .map(StringUtils::trim)
        .filter(StringUtils::isNotBlank)
        .map(url -> Try.of(() -> urlFactory.createURL(url))
            .onFailure(e -> log.error("Error reading url ".concat(url), e)).getOrNull())
        .flatMap(urlLinesReader::loadLines);
  }

  public Stream<String> loadDenyLines() throws IOException {
    return Arrays.stream(resourcePatternResolver
            .getResources(denyListClasspath))
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
