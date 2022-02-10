package co.com.asl.firewall.command;

import co.com.asl.firewall.configuration.UFWOperation;
import co.com.asl.firewall.entities.CIDRAddressV4;
import org.apache.commons.collections4.Transformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

@Scope("prototype")
@Component
public class RuleUfwUserRulesTransformer implements Transformer<CIDRAddressV4, Stream<String>> {
    private final UFWOperation operation;

    @Autowired
    public RuleUfwUserRulesTransformer(UFWOperation operation) {
        this.operation = operation;
    }

    @Override
    public Stream<String> transform(CIDRAddressV4 cidrAddressV4) {
        Collection<String> lines = new ArrayList<>();
        lines.add("");
        lines.add(String.format("### tuple ### %s any any 0.0.0.0/0 any %s in", operation.policy().toLowerCase(), cidrAddressV4.toString()));
        lines.add(String.format("-A ufw-user-input -s %s -j %s", cidrAddressV4, operation.name().toUpperCase()));
        lines.add("");
        lines.add(String.format("### tuple ### %s any any %s any 0.0.0.0/0 out", operation.policy().toLowerCase(), cidrAddressV4.toString()));
        lines.add(String.format("-A ufw-user-output -d %s -j %s", cidrAddressV4, operation.name().toUpperCase()));
        return lines.stream();
    }
}
