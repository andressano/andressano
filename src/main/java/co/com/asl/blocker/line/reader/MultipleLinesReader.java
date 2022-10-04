package co.com.asl.blocker.line.reader;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Stream;

public interface MultipleLinesReader<T> {

  Stream<String> loadLines(Collection<T> resources) throws IOException;
}
