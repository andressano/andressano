package co.com.asl.blocker.file;

import io.vavr.CheckedFunction1;
import io.vavr.control.Option;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

public abstract class LocalHostListLoader extends HostListLoader {

  @Autowired
  protected ResourcePatternResolver resourcePatternResolver;

  protected Stream<String> loadLocalHostsLines(String folder) throws IOException {
    return Arrays.stream(resourcePatternResolver
                    .getResources(String.format("classpath:/META-INF/%s/*.txt", folder)))
                .map(CheckedFunction1.lift(Resource::readableChannel))
                .map(Option::getOrNull)
                .filter(Objects::nonNull)
        .flatMap(rbc -> super.loadHostsLines(rbc));
  }
}
