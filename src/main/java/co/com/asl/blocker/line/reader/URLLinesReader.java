package co.com.asl.blocker.line.reader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
      log.error(e.getLocalizedMessage(), e);
    }
    return Stream.empty();
  }
}
