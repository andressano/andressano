package co.com.asl.blocker.file;

import java.io.BufferedReader;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.stream.Stream;

public final class ResourceUtils {

  private ResourceUtils() {
  }

  public static final Stream<String> loadLines(ReadableByteChannel channel) {
    return new BufferedReader(
        Channels.newReader(channel, Charset.defaultCharset())).lines();
  }

  public static final Stream<String> loadLines(InputStream inputStream) {
    return loadLines(Channels.newChannel(inputStream));
  }
}
