package co.com.asl.blocker.line.reader;

import static org.junit.jupiter.api.Assertions.*;

import co.com.asl.blocker.config.Config;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = InputStreamLinesReaderTest.class)
@Configuration
@Import(InputStreamLinesReader.class)
class InputStreamLinesReaderTest {

  @Autowired
  private InputStreamLinesReader inputStreamLinesReader;

  @Test
  void loadLinesOneLine() {
    final String HELLO_WORLD = "Hello world!";
    final String output = inputStreamLinesReader.loadLines(
        new ByteArrayInputStream((HELLO_WORLD.getBytes()))).findFirst().get();
    assertEquals(HELLO_WORLD, output);
  }

  @Test
  void loadLinesMultipleLines() {
    final String HELLO_WORLD = "Hello world!\r\nThis is good!";
    assertEquals(2, inputStreamLinesReader.loadLines(
        new ByteArrayInputStream((HELLO_WORLD.getBytes()))).count());
  }

  @Test
  void loadLinesEmptyError() {
    try {
      inputStreamLinesReader.loadLines(null);
    } catch (Exception e) {
      assertInstanceOf(RuntimeException.class, e);
    }
  }
}