package co.com.asl.blocker.file;

import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourcePatternResolver;

public abstract class RemoteHostListLoader extends HostListLoader {

  @Autowired
  protected ResourcePatternResolver resourcePatternResolver;

  private ReadableByteChannel openStream(String url) {
    try {
      return Channels.newChannel(new URL(url).openStream());
    } catch (IOException e) {
      return null;
    }
  }

  protected Stream<String> loadRemoteHostsLines(String urlListPath) throws IOException {
    return
        Files.lines(Path.of(resourcePatternResolver
                .getResource(urlListPath).getURI()))
            .map(this::openStream)
            .filter(Objects::nonNull)
            .flatMap(rbc -> super.loadHostsLines(rbc));
  }
}
