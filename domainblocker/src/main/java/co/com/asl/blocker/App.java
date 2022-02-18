package co.com.asl.blocker;

import co.com.asl.blocker.controller.AntiAdsController;
import co.com.asl.blocker.enums.Operation;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {

  public static void main(String[] args) {
    try {
      Operation operation = Operation.INVALID_OPERATION;
      CommandLineParser parser = new GnuParser();
      Options options = createOptions();
      CommandLine line = parser.parse(options, args, true);
      if (line.hasOption("enable")) {
        operation = Operation.CREATE_HOSTS_FILE;
      } else if (line.hasOption("disable")) {
        operation = Operation.DEFAULT_HOSTS_FILE;
      }
      initLog(line);
      runSpringContext(line, operation);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void initLog(CommandLine line) {
    LoggerContext loggerContext = Configurator.initialize("log4j2", "META-INF/log4j2.xml");
    if (line.hasOption("nolog")) {
      loggerContext.getConfiguration().stop();
    }
  }

  private static void runSpringContext(CommandLine line, Operation operation) throws IOException {
    ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(
        "co.com.asl.blocker.config");
    context.start();
    AntiAdsController antiADSController = context.getBean(AntiAdsController.class);
    antiADSController.process(line.getOptionValue("file"), operation);
    context.close();
  }

  private static final Options createOptions() {
    Options options = new Options();
    OptionBuilder.withArgName("hostsFile");
    OptionBuilder.hasArg();
    OptionBuilder.isRequired(true);
    OptionBuilder.withDescription("Creates a new hosts file.");
    options.addOption(OptionBuilder.create("file"));
    options.addOption("disable", false, "Create a default smaller hosts file.");
    options.addOption("enable", false, "Create a combined hosts file.");
    options.addOption("nolog", false, "Disable logging.");
    return options;
  }
}
