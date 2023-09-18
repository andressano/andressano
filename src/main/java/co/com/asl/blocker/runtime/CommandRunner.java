package co.com.asl.blocker.runtime;

import co.com.asl.blocker.line.reader.InputStreamLinesReader;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommandRunner {

  @Autowired
  private InputStreamLinesReader inputStreamLinesReader;

  @Setter
  @Getter
  private int timeout;

  public CommandRunner() {
    setTimeout(30);
  }

  public synchronized Stream<String> execute(String command) {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command);
      processBuilder.redirectErrorStream(true);
      Process process = processBuilder.start();
      boolean result = process.waitFor(getTimeout(), TimeUnit.SECONDS);
      if (!result) {
        return Stream.empty();
      }
      if (process.exitValue() != 0) {
        log.error(IOUtils.toString(process.getInputStream(), Charset.defaultCharset()));
      }
      return inputStreamLinesReader.loadLines(process.getInputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      Thread.currentThread().interrupt();
    }
    return Stream.empty();
  }
}
