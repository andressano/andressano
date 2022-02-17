package co.com.asl.blocker.file;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

@Component
public class SitesToHostsLoader extends RemoteHostListLoader {

  @Autowired
  private ResourcePatternResolver resourcePatternResolver;

  @Override
  public Stream<String> loadHostsLines() throws IOException {
    return super.loadRemoteHostsLines("classpath:/META-INF/sites.txt");
  }
}
