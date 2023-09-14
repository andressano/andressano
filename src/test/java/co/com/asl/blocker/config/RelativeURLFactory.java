package co.com.asl.blocker.config;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;

public class RelativeURLFactory implements URLFactory {

  @Override
  public URL createURL(String url) throws MalformedURLException {
    URI uri = Path.of(".").toAbsolutePath().toUri();
    URL baseURL = uri.toURL();
    return new URL(baseURL, url);
  }
}
