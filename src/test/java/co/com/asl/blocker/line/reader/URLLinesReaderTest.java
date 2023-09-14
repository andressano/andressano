package co.com.asl.blocker.line.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = URLLinesReaderTest.class)
@Configuration
@Import({
    URLLinesReader.class,
    InputStreamLinesReader.class})
class URLLinesReaderTest {

  @Autowired
  private URLLinesReader urlLinesReader;

  @BeforeAll
  static void config() {
    Configurator.setLevel("co.com.asl.blocker", Level.DEBUG);
  }

  @Test
  void loadLines() throws Exception {
    URL url = Path.of("src/test/resources/files/5_lines_file.txt").toUri().toURL();
    assertEquals(5, urlLinesReader.loadLines(url).count());
  }

  @Test
  void fakeURL() throws Exception {
    URL url = Path.of("/this/is/a/fake/file").toUri().toURL();
    assertThrows(RuntimeException.class, () -> urlLinesReader.loadLines(url));
  }
}