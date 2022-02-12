package co.com.asl.blocker.file;

import co.com.asl.blocker.file.line.LineFunctions;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HostsFileTransformer {

  @Autowired
  @Qualifier("localhostName")
  private String hostName;
  @Autowired
  private ResourceLoader resourceLoader;

  public List<String> transform() {
    try {
      Resource hostsFileResource = resourceLoader.getResource(
          "classpath:/META-INF/hostsFiles/linux.txt");
      return Files.lines(Paths.get(hostsFileResource.getURI()))
          .map(LineFunctions.replaceHostName(hostName)).collect(
              Collectors.toList());
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
    return Collections.emptyList();
  }
}
