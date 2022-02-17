package co.com.asl.blocker.file;

import java.io.IOException;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class WhitelistLoader extends LocalHostListLoader {

  @Override
  public Stream<String> loadHostsLines() throws IOException {
    return super.loadLocalHostsLines("whitelist")
        .filter(StringUtils::isNotBlank);
  }
}
