package co.com.asl.blocker.line.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component
public class InputStreamLinesReader implements MultipleLinesReader<InputStream> {

  @Override
  public Stream<String> loadLines(Collection<InputStream> resources) throws IOException {
    Collection<String> lines = new ArrayList<>();
    resources.forEach(i -> {
      try (Scanner scanner = new Scanner(i)) {
        while (scanner.hasNextLine()) {
          lines.add(scanner.nextLine());
        }
      }
    });
    return lines.stream();
  }
}
