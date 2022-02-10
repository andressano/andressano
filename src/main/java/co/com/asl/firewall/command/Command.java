package co.com.asl.firewall.command;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Command {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private String commandLine;

    public Command() {
        super();
    }

    public Command(String commandLine) {
        super();
        setCommandLine(commandLine);
    }

    public String getCommandLine() {
        return commandLine;
    }

    private void setCommandLine(String commandLine) {
        this.commandLine = commandLine;
    }

    public String execute() {
        try {
            String[] commandSyntax = new String[]{"/bin/bash", "-c", "timeout 10s ".concat(getCommandLine())};
            Process process = Runtime.getRuntime().exec(commandSyntax);
            String output = IOUtils.toString(process.getInputStream());
            output = output.replaceAll("[\r\t\n]+", " ").trim();
            if (log.isTraceEnabled()) log.trace("{}, Message: '{}'", getCommandLine(), output);
            return output;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "";
    }

}
