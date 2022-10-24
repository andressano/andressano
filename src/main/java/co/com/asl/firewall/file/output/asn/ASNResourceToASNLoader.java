package co.com.asl.firewall.file.output.asn;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.com.asl.firewall.configuration.FWOperation;
import co.com.asl.firewall.configuration.FirewallType;
import co.com.asl.firewall.entities.ASNumber;
import co.com.asl.firewall.file.ASNListLoader;
import co.com.asl.firewall.file.output.ResourceToASNListLoader;

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
  public Stream<ASNumber> load(FirewallType firewallType, String setting, FWOperation ufwOperation) {
    return asnListLoader.load(resourceToASNListLoader.load(firewallType, setting, ufwOperation))
        .map(ASNumber::transform)
        .sorted();
  }
}
