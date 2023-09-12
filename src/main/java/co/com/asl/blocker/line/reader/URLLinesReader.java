package co.com.asl.blocker.line.reader;

import io.vavr.control.Try;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class URLLinesReader implements LinesReader<URL> {

  @Autowired
  private InputStreamLinesReader linesReader;

  @Override
  public Stream<String> loadLines(URL url) {
    if (log.isDebugEnabled()) {
      log.debug("Reading {}", url.toString());
    }
    try (InputStream is = url.openStream()) {
      return linesReader.loadLines(is);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
