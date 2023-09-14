package co.com.asl.blocker.config;

import static org.junit.jupiter.api.Assertions.*;

import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.Test;

class RemoteURLFactoryTest {

  @Test
  void reachSite() throws MalformedURLException {
    final String SITE = "https://www.google.com";
    RemoteURLFactory remoteURLFactory = new RemoteURLFactory();
    URL url = remoteURLFactory.createURL(SITE);
    assertEquals(SITE, url.toString());
  }

}