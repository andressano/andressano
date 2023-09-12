package co.com.asl.blocker.line;

import static org.junit.jupiter.api.Assertions.*;

import javax.sound.sampled.Line;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class LineFunctionsTest {

  @Test
  void removeComments() {
    final String LINE = "This is a line. ";
    final String COMMENT = "# With comments. ";
    assertEquals(LINE, LineFunctions.removeComments(StringUtils.join(LINE, COMMENT)));
  }

  @Test
  void removeCommentsNoComments() {
    final String LINE = "This is a line. ";
    assertEquals(LINE, LineFunctions.removeComments(LINE));
  }

  @Test
  void removeIp() {
    final String LINE = "255.255.255.255";
    final String BLANK = "  \t\r\n";
    assertEquals(BLANK, LineFunctions.removeIp(StringUtils.join(LINE, BLANK)));
  }

  @Test
  void removeIp0_0_0_0() {
    final String LINE = "0.0.0.0";
    assertEquals("", LineFunctions.removeIp(LINE));
  }

  @Test
  void isValidHostnameLine() {
    assertTrue(LineFunctions.isValidHostnameLine("test.testonline.com"));
  }

  @Test
  void isValidHostnameLineFail() {
    assertFalse(LineFunctions.isValidHostnameLine("test"));
  }

  @Test
  void formatLine() {
    final String host = "test.testing.com";
    assertEquals(StringUtils.join(LineConstants.ROUTE_IP,"\t",host), LineFunctions.formatLine(host));
  }
}