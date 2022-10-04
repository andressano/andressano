package co.com.asl.blocker.line.generation;

import co.com.asl.blocker.enums.Operation;
import co.com.asl.blocker.host.Blacklist;
import co.com.asl.blocker.host.HostList;
import co.com.asl.blocker.host.Whitelist;
import co.com.asl.blocker.line.LineFunctions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.Stream;

@Component
@Slf4j
public class BlackListLinesCreator implements LinesCreator {

    @Autowired
    private Blacklist blacklist;
    @Autowired
    private HostList hostList;
    @Autowired
    private Whitelist whitelist;

    private boolean isValid(String host) {
        return whitelist.stream().noneMatch(host::endsWith) || whitelist.stream().filter(h -> !h.startsWith(".")).allMatch(host::equalsIgnoreCase);
    }

    public Stream<String> create() throws IOException {
        return Stream.concat(blacklist.stream(), hostList.stream()).filter(this::isValid).map(LineFunctions::formatLine);
    }

    @Override
    public int priority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isOperationAllowed(Operation operation) {
        return Operation.CREATE_HOSTS_FILE.equals(operation) || Operation.DEFAULT_HOSTS_FILE.equals(operation);
    }
}
