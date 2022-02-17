package co.com.asl.blocker.file;

import co.com.asl.blocker.file.line.LineConstants;
import java.io.IOException;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

@Component
public class PreludeLinesLoader implements LinesLoader {

  @Autowired
  @Qualifier("osName")
  private String osName;
  @Autowired
  @Qualifier("hostName")
  private String hostName;
  @Autowired
  private ResourcePatternResolver resourcePatternResolver;

  public Stream<String> loadLines() throws IOException {
    return LinesLoader.loadLines(
            resourcePatternResolver.getResource(
                String.format("classpath:/META-INF/hostsFiles/%s.txt", osName)).getInputStream())
        .map(l -> l.replace(LineConstants.COMPUTER_NAME, hostName));
  }
}
