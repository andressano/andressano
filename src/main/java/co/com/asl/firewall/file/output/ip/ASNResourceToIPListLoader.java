package co.com.asl.firewall.file.output.ip;

import java.util.Collection;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.com.asl.firewall.configuration.FWOperation;
import co.com.asl.firewall.configuration.FirewallType;
import co.com.asl.firewall.entities.CIDRAddressV4;
import co.com.asl.firewall.file.ASNListLoader;
import co.com.asl.firewall.file.output.ResourceToASNListLoader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ASNResourceToIPListLoader implements IPListLoader {
  private final ResourceToASNListLoader resourceToASNListLoader;
  private final ASNListLoader asnListLoader;

  @Autowired
  public ASNResourceToIPListLoader(
      ResourceToASNListLoader resourceToASNListLoader,
      ASNListLoader asnListLoader) {
    this.resourceToASNListLoader = resourceToASNListLoader;
    this.asnListLoader = asnListLoader;
  }

  public Stream<CIDRAddressV4> load(FirewallType firewallType, String setting, FWOperation ufwOperation) {
    return asnListLoader.load(
            resourceToASNListLoader
                .load(setting, ufwOperation))
        .flatMap(Collection::stream);
  }
}
