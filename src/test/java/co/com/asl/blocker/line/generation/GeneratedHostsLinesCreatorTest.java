package co.com.asl.blocker.line.generation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.com.asl.blocker.enums.Operation;
import co.com.asl.blocker.line.LineConstants;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = GeneratedHostsLinesCreatorTest.class)
@Configuration
@Import(GeneratedHostsLinesCreator.class)
class GeneratedHostsLinesCreatorTest {

  @Autowired
  private GeneratedHostsLinesCreator generatedHostsLinesCreator;

  @Test
  void create() throws IOException {
    assertTrue(
        generatedHostsLinesCreator.create().anyMatch(s -> s.equals(LineConstants.GENERATED_HOSTS)));
  }

  @Test
  void priority() {
    assertEquals(0, generatedHostsLinesCreator.priority());
  }

  @Test
  void isOperationAllowedCreate() {
    assertTrue(generatedHostsLinesCreator.isOperationAllowed(Operation.CREATE_HOSTS_FILE));
  }

  @Test
  void isOperationAllowedDefault() {
    assertTrue(generatedHostsLinesCreator.isOperationAllowed(Operation.DEFAULT_HOSTS_FILE));
  }

  @Test
  void isOperationAllowedInvalid() {
    assertFalse(generatedHostsLinesCreator.isOperationAllowed(Operation.INVALID_OPERATION));
  }
}