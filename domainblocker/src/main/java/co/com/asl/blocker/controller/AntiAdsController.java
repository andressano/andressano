package co.com.asl.blocker.controller;

import co.com.asl.blocker.enums.Operation;
import co.com.asl.blocker.file.HostsFileTransformer;
import co.com.asl.blocker.file.URLToLinesTransformer;
import co.com.asl.blocker.file.line.LineConstants;
import co.com.asl.blocker.file.line.LinePredicates;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AntiAdsController {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Value("#{ blacklist }")
    private List<String> blacklist;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private HostsFileTransformer hostsFileTransformer;
    @Autowired
    private URLToLinesTransformer urlToLinesTransformer;

    public void process(File hostsFile, Operation operation) throws IOException {
        Assert.notNull(hostsFile);
        Assert.notNull(operation);

        StopWatch stopWatch = new StopWatch();
        logger.info("Creating file {}", hostsFile.getAbsolutePath());
        stopWatch.start();

        List<String> fileLines = hostsFileTransformer.transform();
        fileLines.addAll(createPreludeLines());
        FileUtils.deleteQuietly(hostsFile);
        FileUtils.writeLines(hostsFile, fileLines);
        fileLines.clear();

        Set<String> hostsLines = new TreeSet<>();
        if (Operation.CREATE_HOSTS_FILE.equals(operation)) {
            Resource resource = resourceLoader.getResource("classpath:/META-INF/antiads/sites.txt");

            Set<String> hosts = IOUtils.readLines(resource.getInputStream(), Charset.defaultCharset()).stream().filter(LinePredicates.isNotComment()).map(urlToLinesTransformer::transform).flatMap(Set::stream).collect(Collectors.toSet());
            hostsLines.addAll(hosts);
            hostsLines.addAll(blacklist);
        }
        hostsLines.addAll(blacklist);
        fileLines.addAll(hostsLines.stream().map(l -> String.format("%s\t%s", LineConstants.ROUTE_IP, l)).collect(Collectors.toList()));
        FileUtils.writeLines(hostsFile, fileLines, true);
        stopWatch.stop();
        logger.info("File {} was created in {} seconds", hostsFile.getAbsolutePath(), Long.valueOf(stopWatch.getTime() / 1000L));
    }

    private Set<String> createPreludeLines() {
        return new HashSet<>(Arrays.asList("", "# Generated hosts"));
    }
}
