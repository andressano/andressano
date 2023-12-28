package co.com.asl.firewall.configuration;

import java.io.IOException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StopWatch;

@Slf4j
@Getter
public abstract class AbstractConfigurator {
  @Value("#{profileOption}")
  protected String profile;
  @Value("#{rulesPath}")
  protected String path;

  protected abstract void writeFile() throws IOException;

  public void configure() throws IOException {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    log.info("UFW configuration '{}' started", profile);
    writeFile();
    stopWatch.stop();
    log.info("UFW configuration '{}' finished in {} seconds", profile,
        stopWatch.getTotalTimeSeconds());
  }
}
