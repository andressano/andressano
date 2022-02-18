package co.com.asl.blocker.host;

import co.com.asl.blocker.line.LineFunctions;
import co.com.asl.blocker.line.reader.ResourceLinesReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

@Component
public class BlacklistLoader implements Blacklist {

  @Autowired
  protected ResourcePatternResolver resourcePatternResolver;
  @Autowired
  private ResourceLinesReader resourceLinesReader;

  public Stream<String> loadLines() throws IOException {
    return resourceLinesReader.loadLines(Arrays.asList(resourcePatternResolver
            .getResources("classpath:/META-INF/blacklist/*.txt")))
        .map(LineFunctions::removeComments)
        .map(StringUtils::trimToEmpty)
        .filter(StringUtils::isNotBlank)
        .filter(LineFunctions::isValidLine);
  }

}
