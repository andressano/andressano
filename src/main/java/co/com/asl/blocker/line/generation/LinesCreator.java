package co.com.asl.blocker.line.generation;

import co.com.asl.blocker.enums.Operation;
import java.io.IOException;
import java.util.stream.Stream;

public interface LinesCreator {

  Stream<String> create() throws IOException;

  int priority();

  default boolean isOperationAllowed(Operation operation){
    return true;
  }
}
