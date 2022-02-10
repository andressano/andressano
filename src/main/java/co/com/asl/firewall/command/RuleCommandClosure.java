package co.com.asl.firewall.command;

import co.com.asl.firewall.configuration.UFWOperation;
import co.com.asl.firewall.entities.CIDRAddressV4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class RuleCommandClosure extends AbstractCommandClosure<CIDRAddressV4> {
	@Autowired
	public RuleCommandClosure(UFWOperation operation) {
		super(String.format("ufw %s out from any to ", operation.toString()).concat("%s"));
	}
}
