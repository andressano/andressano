package co.com.asl.blocker.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

@Component
public class PreludeLinesLoader {

  @Autowired
  @Qualifier("osName")
  private String osName;
  @Autowired
  @Qualifier("hostName")
  private String hostName;
  @Autowired
  private ResourcePatternResolver resourcePatternResolver;

  public String load() throws IOException {
    return Files.readString(
            Path.of(
                resourcePatternResolver
                    .getResource(String.format("classpath:/META-INF/hostsFiles/%s.txt", osName))
                    .getURI()))
        .replace("%%COMPUTER_NAME%%", hostName);
  }
}
