package co.com.asl.blocker.host;

import java.io.IOException;
import java.util.stream.Stream;

public interface HostList {

  Stream<String> loadLines() throws IOException;
}
