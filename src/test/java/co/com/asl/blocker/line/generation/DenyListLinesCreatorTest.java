package co.com.asl.blocker.line.generation;

import static org.junit.jupiter.api.Assertions.*;

import co.com.asl.blocker.config.RelativeURLFactory;
import co.com.asl.blocker.config.RemoteURLFactory;
import co.com.asl.blocker.config.URLFactory;
import co.com.asl.blocker.enums.Operation;
import co.com.asl.blocker.host.AllowList;
import co.com.asl.blocker.host.HostList;
import co.com.asl.blocker.line.reader.InputStreamLinesReader;
import co.com.asl.blocker.line.reader.ResourceLinesReader;
import co.com.asl.blocker.line.reader.URLLinesReader;
import java.io.IOException;
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
@ContextConfiguration(classes = DenyListLinesCreatorTest.class)
@Configuration
@Import({
    AllowList.class,
    DenyListLinesCreator.class,
    HostList.class,
    InputStreamLinesReader.class,
    ResourceLinesReader.class,
    URLLinesReader.class
})
class DenyListLinesCreatorTest {

  @Autowired
  private DenyListLinesCreator denyListLinesCreator;

  @Bean("sitesClasspath")
  public String sitesClasspath() {
    return "classpath:/lists/sites.txt";
  }

  @Bean("denyListClasspath")
  public String denyListClasspath() {
    return "classpath:/lists/deny-list/*.txt";
  }

  @Bean("allowListClasspath")
  public String allowListClasspath() {
    return "classpath:/lists/allow-list/*.txt";
  }

  @Bean
  public URLFactory urlFactory() {
    return new RelativeURLFactory();
  }

  @BeforeAll
  static void config() {
    Configurator.setLevel("co.com.asl.blocker", Level.DEBUG);
  }

  @Test
  void create() throws IOException {
    assertTrue(denyListLinesCreator.create().count() > 0);
  }

  @Test
  void priority() {
    assertEquals(Integer.MAX_VALUE, denyListLinesCreator.priority());
  }

  @Test
  void isOperationAllowed() {
    assertTrue(denyListLinesCreator.isOperationAllowed(Operation.CREATE_HOSTS_FILE));
  }

  @Test
  void isOperationAllowedInvalid() {
    assertFalse(denyListLinesCreator.isOperationAllowed(Operation.INVALID_OPERATION));
  }
}