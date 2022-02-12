package co.com.asl.blocker.file;

import co.com.asl.blocker.file.line.LineFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

@Component
public class HostsFileTransformer {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("localhostName")
    private String hostName;
    @Autowired
    private ResourceLoader resourceLoader;

    public List<String> transform() {
        try {
            Resource hostsFileResource = resourceLoader.getResource("classpath:/META-INF/hostsFiles/linux.txt");
            Scanner scanner = new Scanner(hostsFileResource.getInputStream());
            List<String> list = new ArrayList<>();
            while (scanner.hasNextLine())
                list.add(LineFunctions.replaceHostName(hostName).apply(scanner.nextLine()));
            return list;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }
}
