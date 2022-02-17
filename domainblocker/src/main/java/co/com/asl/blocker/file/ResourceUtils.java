package co.com.asl.blocker.file;

import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import java.util.stream.Stream;

public final class ResourceUtils {

  private ResourceUtils() {
  }

  public static final Stream<String> loadLines(ReadableByteChannel channel) {
    return loadLines(Channels.newInputStream(channel));
  }

  public static final Stream<String> loadLines(InputStream inputStream) {
    Collection<String> lines = new ArrayList<>();
    Scanner scanner = new Scanner(inputStream);
    while (scanner.hasNextLine()) {
      lines.add(scanner.nextLine());
    }
    scanner.close();
    return lines.stream();
  }
}
