package co.com.asl.blocker.line.generation;

import co.com.asl.blocker.enums.Operation;
import co.com.asl.blocker.host.AllowList;
import co.com.asl.blocker.host.HostList;
import co.com.asl.blocker.line.LineFunctions;
import io.vavr.Predicates;
import java.io.IOException;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DenyListLinesCreator implements LinesCreator {

  @Autowired
  private HostList hostList;
  @Autowired
  private AllowList allowList;

  private boolean isValid(String host) {
    return allowList.stream()
        .noneMatch(Predicates.anyOf(host::equals, h -> host.endsWith(".".concat(h))));
  }

  public Stream<String> create() throws IOException {
    if (log.isDebugEnabled()) {
      log.debug("Creating hosts list");
    }

    return hostList.stream().filter(this::isValid)
        .map(LineFunctions::formatLine);
  }

  @Override
  public int priority() {
    return Integer.MAX_VALUE;
  }

  @Override
  public boolean isOperationAllowed(Operation operation) {
    return Operation.CREATE_HOSTS_FILE.equals(operation);
  }
}
