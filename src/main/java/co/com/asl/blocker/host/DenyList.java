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
public class DenyList extends TreeSet<String> {

    @Autowired
    protected ResourcePatternResolver resourcePatternResolver;
    @Autowired
    private ResourceLinesReader resourceLinesReader;

    @PostConstruct
    public void loadLines() throws IOException {
        addAll(resourceLinesReader.loadLines(Arrays.asList(resourcePatternResolver
                        .getResources("classpath:/META-INF/deny-list/*.txt")))
                .map(LineFunctions::removeComments)
                .map(StringUtils::trimToEmpty)
                .filter(StringUtils::isNotBlank)
                .filter(LineFunctions::isValidLine).collect(Collectors.toList()));
    }

}
