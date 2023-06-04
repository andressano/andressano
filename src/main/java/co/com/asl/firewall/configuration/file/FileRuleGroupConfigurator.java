package co.com.asl.firewall.configuration.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import co.com.asl.firewall.configuration.AbstractConfigurator;
import co.com.asl.firewall.configuration.FWOperation;
import co.com.asl.firewall.configuration.FirewallType;
import co.com.asl.firewall.entities.CIDRAddressV4;
import co.com.asl.firewall.entities.transform.CIDRTransformableSet;
import co.com.asl.firewall.file.output.ip.IPListLoader;

@Scope("prototype")
@Component
public final class FileRuleGroupConfigurator extends AbstractConfigurator {

    @Autowired
    private Collection<IPListLoader> listLoaders;

    public FileRuleGroupConfigurator(String profile, String outputFile) {
        super(profile, outputFile);
    }

    private List<String> joinAddresses(List<String> addresses, final int size) {
        List<String> result = new ArrayList<>();
        final String SEPARATOR = ",";
        int group = 0;
        StringBuilder sb = new StringBuilder(size);
        for (final String ip : addresses) {
            final int proposedLength = sb.length() + ip.length() + SEPARATOR.length();
            if (proposedLength <= size)
                sb.append(ip.concat(SEPARATOR));
            if (proposedLength > size) {
                result.add(String.format("GROUP %d:", ++group));
                sb.deleteCharAt(sb.length() - SEPARATOR.length());
                result.add(sb.toString());
                sb.delete(0, sb.length());
            }
        }
        return result;

    }


    private Collection<String> loadRulesLines() {
        Collection<String> addressRulesLines = new ArrayList<>();
        for (FWOperation ufwOperation : FWOperation.values()) {
            ArrayList<String> addresses = listLoaders
                    .stream()
                    .flatMap(ll -> ll.load(FirewallType.UFW, getProfile(), ufwOperation))
                    .collect(Collectors.toCollection(CIDRTransformableSet::new))
                    .transform()
                    .stream()
                    .map(CIDRAddressV4::toString)
                    .collect(Collectors.toCollection(ArrayList::new));

            addressRulesLines.add(ufwOperation.name().concat(":"));
            addressRulesLines.addAll(joinAddresses(addresses, 32767));
        }
        return addressRulesLines;
    }

    @Override
    protected void writeFile() throws IOException {
        final Path userRulesPath = Path.of(getOutputFile());
        Files.write(userRulesPath, loadRulesLines(), StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }
}
