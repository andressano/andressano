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
public class ResourceLinesReader implements MultipleLinesReader<Resource> {

  @Autowired
  private InputStreamLinesReader linesReader;

  @Override
  public Stream<String> loadLines(Collection<Resource> resources) throws IOException {
    Collection<InputStream> inputStreams = resources.stream()
        .filter(Objects::nonNull)
        .map(resource -> Try.of(resource::getInputStream)
            .onFailure(e -> log.error(e.getLocalizedMessage(), e))
            .getOrNull())
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    return linesReader.loadLines(inputStreams);
  }
}
