package co.com.asl.blocker.line.generation;

import co.com.asl.blocker.line.LineConstants;
import co.com.asl.blocker.line.reader.ResourceLinesReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PreludeLinesCreator implements LinesCreator {

  @Autowired
  @Qualifier("osName")
  private String osName;
  @Autowired
  @Qualifier("hostName")
  private String hostName;
  @Autowired
  private ResourcePatternResolver resourcePatternResolver;
  @Autowired
  private ResourceLinesReader resourceLinesReader;

  @Override
  public Stream<String> create() throws IOException {
    if (log.isDebugEnabled()) {
      log.debug("Creating prelude hosts file");
    }

    String path = String.format("classpath:/META-INF/hostsFiles/%s.txt", osName);
    return Arrays.stream(resourcePatternResolver.getResources(path))
        .flatMap(resourceLinesReader::loadLines)
        .map(l -> l.replace(LineConstants.COMPUTER_NAME, hostName));
  }

  @Override
  public int priority() {
    return Integer.MIN_VALUE;
  }

}
