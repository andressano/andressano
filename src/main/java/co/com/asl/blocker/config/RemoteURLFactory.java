package co.com.asl.blocker.config;

import java.net.MalformedURLException;
import java.net.URL;

public class RemoteURLFactory implements URLFactory {

  @Override
  public URL createURL(String url) throws MalformedURLException {
    return new URL(url);
  }

}
