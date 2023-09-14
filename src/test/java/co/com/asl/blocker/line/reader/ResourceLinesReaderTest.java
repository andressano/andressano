package co.com.asl.blocker.line.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URL;
import java.nio.file.Path;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.UrlResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ResourceLinesReaderTest.class)
@Configuration
@Import({ResourceLinesReader.class,
    InputStreamLinesReader.class})
class ResourceLinesReaderTest {

  @Autowired
  private ResourceLinesReader resourceLinesReader;

  @BeforeAll
  static void config() {
    Configurator.setLevel("co.com.asl.blocker", Level.DEBUG);
  }

  @Test
  void loadLines() {
    final String CONTENT = "Lorem ipsum dolor sit amet,\r\nconsectetur adipiscing elit,\r\nsed do eiusmod tempor incididunt ut\r\n labore et dolore magna aliqua.\r\nUt enim ad minim veniam\r\n";
    ByteArrayResource byteArrayResource = new ByteArrayResource(CONTENT.getBytes());
    assertEquals(5, resourceLinesReader.loadLines(byteArrayResource).count());
  }

  @Test
  void fakeURL() throws Exception {
    URL url = Path.of("/this/is/a/fake/file").toUri().toURL();
    UrlResource urlResource = new UrlResource(url);
    assertThrows(RuntimeException.class, () -> resourceLinesReader.loadLines(urlResource));
  }
}