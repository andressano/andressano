package co.com.asl.firewall;

import co.com.asl.firewall.configuration.Config;
import co.com.asl.firewall.configuration.file.FileConfigurator;
import co.com.asl.firewall.configuration.file.FileRuleGroupConfigurator;
import co.com.asl.firewall.configuration.iptables.IpTablesConfigurator;
import co.com.asl.firewall.configuration.ufw.UFWConfigurator;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.Assert;

public class Main {

  private static final Options options = new Options();
  private static final String CONFIG_TYPE_OPTION = "configType";
  private static final String NOLOG_OPTION = "nolog";
  private static final String FIREWALL_OPTION = "firewall";
  private static final String IPTABLES_OPTION = "iptables";
  private static final String FILE_OPTION = "file";
  private static final String GROUPED_FILE_OPTION = "groupedFile";

  static {
    options.addOption(FIREWALL_OPTION, true, "Location of user.rules");
    options.addOption(IPTABLES_OPTION, true, "Location of user.rules for iptables");
    options.addOption(FILE_OPTION, true, "Redirect output into a file");
    options.addOption(GROUPED_FILE_OPTION, true, "Redirect output into a grouped file");
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
    } catch (ParseException | IllegalArgumentException e) {
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
      if(StringUtils.isBlank(configType))
        configType = "default";
      if (cmd.hasOption(FILE_OPTION)) {
        context.getBean(FileConfigurator.class, configType, cmd.getOptionValue(FILE_OPTION))
            .configure();
      } else if (cmd.hasOption(FIREWALL_OPTION)) {
        context.getBean(UFWConfigurator.class, configType, cmd.getOptionValue(FIREWALL_OPTION))
            .configure();
      } else if (cmd.hasOption(IPTABLES_OPTION)) {
        context.getBean(IpTablesConfigurator.class, configType, cmd.getOptionValue(IPTABLES_OPTION))
            .configure();
      } else if (cmd.hasOption(GROUPED_FILE_OPTION)) {
        context.getBean(FileRuleGroupConfigurator.class, configType, cmd.getOptionValue(GROUPED_FILE_OPTION))
            .configure();
      }
    } else {
      printHelp();
    }
    context.stop();
    context.close();
  }
}
