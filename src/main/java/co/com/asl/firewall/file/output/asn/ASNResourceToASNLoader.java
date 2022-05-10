package co.com.asl.firewall.file.output.asn;

import co.com.asl.firewall.configuration.ufw.UFWOperation;
import co.com.asl.firewall.entities.ASNumber;
import co.com.asl.firewall.file.ASNListLoader;
import co.com.asl.firewall.file.output.ResourceToASNListLoader;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ASNResourceToASNLoader implements ASNLoader {

  private final ASNListLoader asnListLoader;
  private final ResourceToASNListLoader resourceToASNListLoader;

  @Autowired
  public ASNResourceToASNLoader(ASNListLoader asnListLoader,
      ResourceToASNListLoader resourceToASNListLoader) {
    this.asnListLoader = asnListLoader;
    this.resourceToASNListLoader = resourceToASNListLoader;
  }

  @Override
  public Stream<ASNumber> load(String setting, UFWOperation ufwOperation) {
    return asnListLoader.load(resourceToASNListLoader.load(setting, ufwOperation))
        .map(ASNumber::transform)
        .sorted();
  }
}
