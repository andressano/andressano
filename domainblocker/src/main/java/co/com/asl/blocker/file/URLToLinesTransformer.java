package co.com.asl.blocker.file;

import co.com.asl.blocker.file.line.LineFunctions;
import co.com.asl.blocker.file.line.LinePredicates;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class URLToLinesTransformer {
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private ResourceLoader resourceLoader;

    public Set<String> transform(String host) {
        Set<String> hostsLines = new HashSet<>();
        if (log.isDebugEnabled())
            log.debug("Loading hosts from {}", host);

        try {
            Resource resourceHost = resourceLoader.getResource(host);
            hostsLines = IOUtils.readLines(resourceHost.getInputStream(), Charset.defaultCharset()).stream()
                    .map(LineFunctions.replaceComments())
                    .filter(s -> !StringUtils.isBlank(s))
                    .map(LineFunctions.removeIp())
                    .map(String::trim)
                    .filter(LinePredicates.hostPredicate())
                    .collect(Collectors.toSet());

            if (log.isDebugEnabled())
                log.debug("File {} has been loaded", host);
        } catch (FileNotFoundException e) {
            log.error("Host ".concat(host).concat(" not found"), e);
        } catch (IOException e) {
            log.error("Host ".concat(host).concat(" couldn't be read"), e);
        }
        return hostsLines;
    }
}
