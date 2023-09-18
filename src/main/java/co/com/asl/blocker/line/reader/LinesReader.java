package co.com.asl.blocker.line.reader;

import java.io.Serializable;
import java.util.stream.Stream;

public interface LinesReader<T> extends Serializable {

  Stream<String> loadLines(T resource);
}
