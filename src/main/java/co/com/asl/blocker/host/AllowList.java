package co.com.asl.blocker.host;

import co.com.asl.blocker.line.LineFunctions;
import co.com.asl.blocker.line.reader.ResourceLinesReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.TreeSet;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

@Component
public class AllowList extends TreeSet<String> {

  @Autowired
  private ResourcePatternResolver resourcePatternResolver;

  @Autowired
  private ResourceLinesReader resourceLinesReader;

  @PostConstruct
  public void loadLines() throws IOException {
    Arrays.stream(resourcePatternResolver
            .getResources("classpath:/META-INF/allow-list/*.txt"))
        .flatMap(resourceLinesReader::loadLines)
        .map(LineFunctions::removeComments)
        .map(StringUtils::trimToEmpty)
        .filter(StringUtils::isNotBlank)
        .forEach(this::add);
  }
}
