package co.com.asl.firewall.file;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Component
public class ResourceLinesTransformer implements Transformer<Resource, Stream<String>> {
    private static final Predicate<String> LINE_PREDICATE = line -> (!StringUtils.isBlank(line))
            && (!StringUtils.startsWith(line.trim(), "#"));
    private final Logger log = LoggerFactory.getLogger(getClass());

    public Stream<String> transform(Resource resource) {
        try {
            return IOUtils.readLines(resource.getInputStream()).stream().filter(LINE_PREDICATE)
                    .map(line -> {
                        StringUtils.removeStart("", "");
                        String tempLine = line;
                        int sharpIndex = tempLine.indexOf('#');
                        if (sharpIndex >= 1)
                            tempLine = tempLine.substring(0, sharpIndex - 1);
                        return tempLine.trim();
                    });
        } catch (IOException e) {
            this.log.error(e.getLocalizedMessage(), e);
        }
        return Stream.empty();
    }
}
