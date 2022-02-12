package co.com.asl.blocker.runtime;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

@Component
public class CommandRunner {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public synchronized String execute(String command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            boolean result = process.waitFor(60, TimeUnit.SECONDS);
            String line = IOUtils.toString(process.getInputStream(), Charset.defaultCharset()).replaceAll("\\s+", " ").trim();
            if (!result) {
                return null;
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug("{} -> {}", command, line);
            }
            return line;
        } catch (Exception e) {
            this.log.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return "";
    }
}
