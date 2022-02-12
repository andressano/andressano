package co.com.asl.blocker.file;

import co.com.asl.blocker.file.line.LineFunctions;
import co.com.asl.blocker.file.line.LinePredicates;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class URLToLinesTransformer {

  @Autowired
  private ResourceLoader resourceLoader;

  public Set<String> transform(String host) {
    try {
      URL url = new URL(host);
      ReadableByteChannel channel = Channels.newChannel(url.openStream());
      BufferedReader br = new BufferedReader(Channels.newReader(channel, Charset.defaultCharset()));
      return br.lines()
          .map(LineFunctions.replaceComments())
          .filter(s -> !StringUtils.isBlank(s))
          .map(LineFunctions.removeIp()).map(String::trim)
          .filter(LinePredicates.hostPredicate())
          .collect(Collectors.toSet());
    } catch (FileNotFoundException e) {
      log.error(String.format("Host %s not found", host), e);
    } catch (IOException e) {
      log.error(String.format("Host %s couldn't be read", host), e);
    }
    return Collections.emptySet();
  }
}
