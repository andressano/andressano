package co.com.asl.blocker.line.generation;

import static org.junit.jupiter.api.Assertions.*;

import co.com.asl.blocker.line.LineConstants;
import co.com.asl.blocker.line.reader.InputStreamLinesReader;
import co.com.asl.blocker.line.reader.ResourceLinesReader;
import co.com.asl.blocker.runtime.CommandRunner;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = PreludeLinesCreatorTest.class)
@Configuration
@Import({
    InputStreamLinesReader.class,
    PreludeLinesCreator.class,
    ResourceLinesReader.class
})
class PreludeLinesCreatorTest {

  @Autowired
  private PreludeLinesCreator preludeLinesCreator;

  @Bean
  public String hostName() {
    return "testHost";
  }

  @Bean
  public String hostFilePath() {
    return "classpath:/files/test-hosts.txt";
  }

  @BeforeAll
  static void config() {
    Configurator.setLevel("co.com.asl.blocker", Level.DEBUG);
  }

  @Test
  void create() throws IOException {
    assertTrue(
        preludeLinesCreator.create().noneMatch(l -> l.contains(LineConstants.COMPUTER_NAME)));
  }

  @Test
  void priority() {
    assertEquals(Integer.MIN_VALUE, preludeLinesCreator.priority());
  }
}