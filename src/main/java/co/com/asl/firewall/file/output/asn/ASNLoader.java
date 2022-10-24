package co.com.asl.firewall.file.output.asn;

import co.com.asl.firewall.configuration.FWOperation;
import co.com.asl.firewall.configuration.FirewallType;
import co.com.asl.firewall.entities.ASNumber;
import java.util.stream.Stream;

public interface ASNLoader {

  Stream<ASNumber> load(FirewallType firewallType, String setting, FWOperation ufwOperation);
}
