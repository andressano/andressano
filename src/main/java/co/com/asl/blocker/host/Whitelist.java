package co.com.asl.blocker.host;

import co.com.asl.blocker.line.LineFunctions;
import co.com.asl.blocker.line.reader.ResourceLinesReader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Component
public class Whitelist extends TreeSet<String> {
    @Autowired
    private ResourcePatternResolver resourcePatternResolver;

    @Autowired
    private ResourceLinesReader resourceLinesReader;

    @PostConstruct
    public void loadLines() throws IOException {
        addAll(resourceLinesReader.loadLines(Arrays.asList(resourcePatternResolver
                        .getResources("classpath:/META-INF/whitelist/*.txt")))
                .map(LineFunctions::removeComments)
                .map(StringUtils::trimToEmpty)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList()));
    }
}
