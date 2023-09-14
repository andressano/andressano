package co.com.asl.blocker.host;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
@ContextConfiguration(classes = AllowListTest.class)
@Configuration
@Import({
    AllowList.class,
    InputStreamLinesReader.class,
    ResourceLinesReader.class
})
class AllowListTest {

  @Autowired
  private AllowList allowList;

  @Bean("allowListClasspath")
  public String allowListClasspath() {
    return "classpath:/lists/allow-list/*.txt";
  }
  @Test
  void loadLines() {
    assertTrue(allowList.size() > 0);
  }
}