package co.com.asl.blocker.file;

import co.com.asl.blocker.file.line.LineFunctions;
import io.vavr.CheckedFunction1;
import io.vavr.control.Option;
import io.vavr.control.Try;
import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

public abstract class HostListLoader {

  protected Stream<String> loadHostsLines(ReadableByteChannel readableByteChannel){
    return ResourceUtils.loadLines(readableByteChannel)
            .map(LineFunctions::removeComments)
            .filter(StringUtils::isNotBlank)
            .map(LineFunctions::removeIp)
            .map(String::trim);
  }

  public abstract Stream<String> loadHostsLines() throws IOException;
}
