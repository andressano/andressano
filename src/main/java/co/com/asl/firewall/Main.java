package co.com.asl.firewall;

import co.com.asl.firewall.configuration.FileConfigurator;
import co.com.asl.firewall.configuration.UFWConfigurator;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;

import java.io.IOException;

public class Main {
    private static final Options options = new Options();

    static {
        options.addOption("firewall", true, "Location of user.rules");
        options.addOption("file", true, "Redirect output into a file");
        options.addOption("configType", true, "Configure UFW with an specific configuration");
        options.addOption("nolog", false, "Turns off current logger");
    }

    public static void main(String[] args) {
        try {
            Assert.notEmpty(args, "Not enough parameters");
            CommandLine cmd = new GnuParser().parse(options, args);
            if (!cmd.hasOption("nolog")) Configurator.initialize("log4j2", "log4j2.xml");
            startSpring(cmd);
        } catch (ParseException e) {
            printHelp();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printHelp() {
        new HelpFormatter().printHelp("java -jar ufw.jar", options);
    }

    private static void startSpring(CommandLine cmd) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/app-context.xml");
        context.start();

        if (cmd.hasOption("configType")) {
            String configType = cmd.getOptionValue("configType");
            if (cmd.hasOption("file")) {
                context.getBean(FileConfigurator.class).configure(configType, cmd.getOptionValue("file"));
            } else if (cmd.hasOption("firewall")) {
                context.getBean(UFWConfigurator.class).configure(configType, cmd.getOptionValue("firewall"));
            }
        } else {
            printHelp();
        }
        context.stop();
        context.close();
    }
}
