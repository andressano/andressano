package co.com.asl.blocker.config;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

public interface URLFactory extends Serializable {

  URL createURL(String url) throws MalformedURLException;
}
