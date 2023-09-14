package co.com.asl.blocker.host;

import static org.junit.jupiter.api.Assertions.assertTrue;

import co.com.asl.blocker.config.RelativeURLFactory;
import co.com.asl.blocker.config.URLFactory;
import co.com.asl.blocker.line.reader.InputStreamLinesReader;
import co.com.asl.blocker.line.reader.ResourceLinesReader;
import co.com.asl.blocker.line.reader.URLLinesReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = HostListTest.class)
@Configuration
@Import({
    HostList.class,
    InputStreamLinesReader.class,
    ResourceLinesReader.class,
    URLLinesReader.class
})
class HostListTest {

  @Autowired
  private HostList hostList;

  @Bean("sitesClasspath")
  public String sitesClasspath() {
    return "classpath:/lists/sites.txt";
  }

  @Bean("denyListClasspath")
  public String denyListClasspath() {
    return "classpath:/lists/deny-list/*.txt";
  }

  @Bean
  public URLFactory urlFactory() {
    return new RelativeURLFactory();
  }

  @Test
  void loadURLLines() throws Exception {
    assertTrue(hostList.size() > 0);
  }

}