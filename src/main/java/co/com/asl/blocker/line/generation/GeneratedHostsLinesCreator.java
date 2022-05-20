package co.com.asl.blocker.line.generation;

import co.com.asl.blocker.enums.Operation;
import co.com.asl.blocker.line.LineConstants;
import java.io.IOException;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component
public class GeneratedHostsLinesCreator implements LinesCreator {

  @Override
  public Stream<String> create() throws IOException {
    return LineConstants.GENERATED_HOSTS;
  }

  @Override
  public int priority() {
    return Integer.MIN_VALUE + 1;
  }

  @Override
  public boolean isOperationAllowed(Operation operation) {
    return Operation.CREATE_HOSTS_FILE.equals(operation) || Operation.DEFAULT_HOSTS_FILE.equals(
        operation);
  }
}
