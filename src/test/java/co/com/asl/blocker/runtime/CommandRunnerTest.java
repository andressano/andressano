package co.com.asl.blocker.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;

import co.com.asl.blocker.line.reader.InputStreamLinesReader;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CommandRunnerTest.class)
@Configuration
@Import({CommandRunner.class,
    InputStreamLinesReader.class})
public class CommandRunnerTest {

  @Autowired
  private CommandRunner commandRunner;

  @Test
  void testExecute() {
    final String HELLO_WORLD = "Hello world!";
    Stream<String> stream = commandRunner.execute("echo '" + HELLO_WORLD + "'");
    String output = stream.findFirst().get();
    assertEquals(HELLO_WORLD, output);
  }

  @Test
  void testExecuteNoOutput() {
    Stream<String> stream = commandRunner.execute("sleep 0");
    assertEquals(0, stream.count());
  }

  @Test
  void testExecuteNoResult() {
    commandRunner.setTimeout(1);
    Stream<String> stream = commandRunner.execute("sleep " + commandRunner.getTimeout() + 1);
    assertEquals(0, stream.count());
  }

  @Test
  void testExecuteIoException() {
    Stream<String> stream = commandRunner.execute("cat /dev/test");
    assertEquals(0, stream.count());
  }
}