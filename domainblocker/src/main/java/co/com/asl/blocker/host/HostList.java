package co.com.asl.blocker.host;

import co.com.asl.blocker.enums.Operation;
import java.io.IOException;
import java.util.stream.Stream;

public interface HostList {

  Stream<String> loadLines() throws IOException;
}
