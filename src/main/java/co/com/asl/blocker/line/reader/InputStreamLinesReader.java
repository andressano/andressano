package co.com.asl.blocker.line.reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Stream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public final class InputStreamLinesReader implements LinesReader<InputStream> {

  @Override
  public Stream<String> loadLines(InputStream is) {
    try (InputStreamReader isr = new InputStreamReader(is)) {
      return IOUtils.readLines(isr).stream()
          .filter(StringUtils::isNotBlank);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
