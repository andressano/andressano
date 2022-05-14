package co.com.asl.firewall.file.output.asn;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.com.asl.firewall.configuration.FWOperation;
import co.com.asl.firewall.configuration.FirewallType;
import co.com.asl.firewall.entities.ASNumber;
import co.com.asl.firewall.file.output.ip.ResourceToIPListLoader;

@Component
public class IPResourceToASNLoader implements ASNLoader {

  private final ResourceToIPListLoader resourceToIPListLoader;

  @Autowired
  public IPResourceToASNLoader(
      ResourceToIPListLoader resourceToIPListLoader) {
    this.resourceToIPListLoader = resourceToIPListLoader;
  }

  @Override
  public Stream<ASNumber> load(FirewallType firewallType, String profile, FWOperation ufwOperation) {
    return Stream.of(resourceToIPListLoader.load(firewallType, profile, ufwOperation)
        .collect(Collectors.toCollection(() -> new ASNumber(0, "Others"))));
  }
}
