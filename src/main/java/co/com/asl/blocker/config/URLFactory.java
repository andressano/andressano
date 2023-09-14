package co.com.asl.blocker.config;

import java.net.MalformedURLException;
import java.net.URL;

public interface URLFactory {

  URL createURL(String url) throws MalformedURLException;
}
