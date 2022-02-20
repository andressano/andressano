package co.com.asl.firewall;

import co.com.asl.firewall.configuration.Config;
import co.com.asl.firewall.configuration.FileConfigurator;
import co.com.asl.firewall.configuration.UFWConfigurator;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.Assert;

public class Main {

  private static final Options options = new Options();
  private static final String FIREWALL_OPTION = "firewall";
  private static final String CONFIG_TYPE_OPTION = "configType";
  private static final String FILE_OPTION = "file";
  private static final String NOLOG_OPTION = "nolog";

  static {
    options.addOption(FIREWALL_OPTION, true, "Location of user.rules");
    options.addOption(FILE_OPTION, true, "Redirect output into a file");
    options.addOption(CONFIG_TYPE_OPTION, true, "Configure UFW with an specific configuration");
    options.addOption(NOLOG_OPTION, false, "Turns off current logger");
  }

  public static void main(String[] args) {
    try {
      Assert.notEmpty(args, "Not enough parameters");
      CommandLine cmd = new GnuParser().parse(options, args);
        if (!cmd.hasOption(NOLOG_OPTION)) {
            Configurator.initialize("log4j2", "log4j2.xml");
        }
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
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
        Config.class);
    context.start();

    if (cmd.hasOption(CONFIG_TYPE_OPTION)) {
      String configType = cmd.getOptionValue(CONFIG_TYPE_OPTION);
      if (cmd.hasOption(FILE_OPTION)) {
        context.getBean(FileConfigurator.class)
            .configure(configType, cmd.getOptionValue(FILE_OPTION));
      } else if (cmd.hasOption(FIREWALL_OPTION)) {
        context.getBean(UFWConfigurator.class)
            .configure(configType, cmd.getOptionValue(FIREWALL_OPTION));
      }
    } else {
      printHelp();
    }
    context.stop();
    context.close();
  }
}
