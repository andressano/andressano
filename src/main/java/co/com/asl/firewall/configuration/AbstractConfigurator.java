package co.com.asl.firewall.configuration;

import java.io.IOException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

@Slf4j
public abstract class AbstractConfigurator {
  @Getter
  public final String profile;
  @Getter
  public final String outputFile;

  protected AbstractConfigurator(String profile, String outputFile) {
    this.profile = profile;
    this.outputFile = outputFile;
  }

  protected abstract void writeFile() throws IOException;

  public void configure() throws IOException {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    log.info("UFW configuration '{}' started", getProfile());
    writeFile();
    stopWatch.stop();
    log.info("UFW configuration '{}' finished in {} seconds", getProfile(),
        stopWatch.getTotalTimeSeconds());
  }
}
