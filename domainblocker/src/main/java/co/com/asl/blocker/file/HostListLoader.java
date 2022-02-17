package co.com.asl.blocker.file;

import co.com.asl.blocker.file.line.LineFunctions;
import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

public abstract class HostListLoader {

  protected Stream<String> loadHostsLines(ReadableByteChannel readableByteChannel) {
    return ResourceUtils.loadLines(readableByteChannel)
        .map(LineFunctions::removeComments)
        .filter(StringUtils::isNotBlank)
        .map(LineFunctions::removeIp)
        .map(String::trim);
  }

  public abstract Stream<String> loadHostsLines() throws IOException;
}
