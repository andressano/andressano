package co.com.asl.firewall.file.output.ip;

import co.com.asl.firewall.configuration.UFWOperation;
import co.com.asl.firewall.entities.CIDRAddressV4;
import co.com.asl.firewall.file.ASNListLoader;
import co.com.asl.firewall.file.output.ResourceToASNListLoader;
import java.util.Collection;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

  public Stream<CIDRAddressV4> load(String string, UFWOperation ufwOperation) {
    return asnListLoader.load(
            resourceToASNListLoader
                .load(string, ufwOperation))
        .flatMap(Collection::stream);
  }
}
