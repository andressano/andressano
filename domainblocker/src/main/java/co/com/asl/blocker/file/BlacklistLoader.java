package co.com.asl.blocker.file;

import co.com.asl.blocker.file.line.LineConstants;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class BlacklistLoader extends LocalHostListLoader {

  private boolean isValidLine(String line) {
    return StringUtils.isNotBlank(line) && line.matches(LineConstants.HOSTNAME_PATTERN);
  }

  @Override
  public Stream<String> loadHostsLines() throws IOException {
    return super.loadLocalHostsLines("blacklist")
        .filter(this::isValidLine);
  }
}
