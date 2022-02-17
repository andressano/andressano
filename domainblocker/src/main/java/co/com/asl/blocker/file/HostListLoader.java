package co.com.asl.blocker.file;

import co.com.asl.blocker.file.line.LineFunctions;
import java.io.InputStream;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

public interface HostListLoader extends LinesLoader {

  static Stream<String> loadLines(InputStream inputStream) {
    return LinesLoader.loadLines(inputStream)
        .map(LineFunctions::removeComments)
        .filter(StringUtils::isNotBlank)
        .map(LineFunctions::removeIp)
        .map(String::trim);
  }
}
