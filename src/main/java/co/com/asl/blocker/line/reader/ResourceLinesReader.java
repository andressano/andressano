package co.com.asl.blocker.line.reader;

import io.vavr.control.Try;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ResourceLinesReader implements LinesReader<Resource> {

  @Autowired
  private InputStreamLinesReader linesReader;

  @Override
  public Stream<String> loadLines(Resource resource) {
    if(log.isDebugEnabled())
      log.debug("Reading {}",resource.getFilename());
    try (InputStream is = resource.getInputStream()) {
      return linesReader.loadLines(is);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
