package co.com.asl.blocker.file;

import java.io.IOException;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component
public class SitesToHostsLoader extends RemoteHostListLoader {

  @Override
  public Stream<String> loadHostsLines() throws IOException {
    return super.loadRemoteHostsLines("classpath:/META-INF/sites.txt");
  }
}
