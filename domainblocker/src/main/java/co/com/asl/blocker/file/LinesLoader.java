package co.com.asl.blocker.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import java.util.stream.Stream;

public interface LinesLoader {

  static Stream<String> loadLines(InputStream inputStream) {
    Collection<String> lines = new ArrayList<>();
    try (Scanner scanner = new Scanner(inputStream)) {
      while (scanner.hasNextLine()) {
        lines.add(scanner.nextLine());
      }
    }
    return lines.stream();
  }

  Stream<String> loadLines() throws IOException;
}
