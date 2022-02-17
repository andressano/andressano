package co.com.asl.blocker.file;

import co.com.asl.blocker.enums.Operation;
import co.com.asl.blocker.file.line.LineConstants;
import co.com.asl.blocker.file.line.LineFunctions;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class BlacklistLoader extends LocalHostListLoader implements Operable {

  @Override
  public Stream<String> loadLines() throws IOException {
    return super.loadLocalHostsLines("classpath:/META-INF/blacklist/*.txt")
        .filter(LineFunctions::isValidLine);
  }

  @Override
  public Collection<Operation> validOperations() {
    return List.of(Operation.CREATE_HOSTS_FILE, Operation.DEFAULT_HOSTS_FILE);
  }
}
