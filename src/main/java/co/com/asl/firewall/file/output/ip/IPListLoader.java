package co.com.asl.firewall.file.output.ip;

import co.com.asl.firewall.configuration.FWOperation;
import co.com.asl.firewall.configuration.FirewallType;
import co.com.asl.firewall.entities.CIDRAddressV4;
import java.util.stream.Stream;

public interface IPListLoader {

  Stream<CIDRAddressV4> load(FirewallType firewallType, String setting, FWOperation ufwOperation);
}
