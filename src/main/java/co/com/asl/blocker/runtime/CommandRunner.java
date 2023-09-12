package co.com.asl.blocker.runtime;

import co.com.asl.blocker.line.reader.InputStreamLinesReader;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

@Slf4j
@Component
public class CommandRunner {

  @Autowired
  private InputStreamLinesReader inputStreamLinesReader;

  public synchronized Stream<String> execute(String command) {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command);
      processBuilder.redirectErrorStream(true);
      Process process = processBuilder.start();
      boolean result = process.waitFor(60, TimeUnit.SECONDS);
      if (!result) {
        return Stream.empty();
      }
      return inputStreamLinesReader.loadLines(process.getInputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      Thread.currentThread().interrupt();
    }
    return Stream.empty();
  }
}
