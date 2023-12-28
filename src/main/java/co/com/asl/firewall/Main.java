package co.com.asl.firewall;

import co.com.asl.firewall.configuration.Config;
import co.com.asl.firewall.configuration.file.FileRuleGroupConfigurator;
import co.com.asl.firewall.configuration.iptables.IpTablesConfigurator;
import co.com.asl.firewall.configuration.ufw.UFWConfigurator;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Visibility;
import picocli.CommandLine.Option;

@Command(name = "firewall")
public class Main implements Callable<Integer> {

  @Option(names = {"-f",
      "--firewall"}, paramLabel = "ufw|ip-tables|file", description = "Kind of firewall rules", showDefaultValue = Visibility.ALWAYS, required = false, defaultValue = "ufw")
  private String firewallOption;

  @Option(names = {"-p",
      "--profile"}, paramLabel = "<profile>", description = "Profile name", showDefaultValue = Visibility.ALWAYS, required = false, defaultValue = "default")
  private String profileOption;

  @Option(names = {"-ll",
      "--log-level"}, description = "Log level for logger", required = false, defaultValue = "info")
  private String logLevel;

  @Option(names = {"-r", "--rules-path"}, required = true, description = "Rules path")
  private String rulesPath;

  public static void main(String[] args) {
    int exitCode = new CommandLine(new Main()).execute(args);
    System.exit(exitCode);
  }

  @Override
  public Integer call() throws Exception {
    Configurator.setLevel("co.com.asl.firewall", Level.valueOf(logLevel));
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
        Config.class);
    context.start();

    switch (firewallOption) {
      case "ufw":
        context.getBean(UFWConfigurator.class, profileOption, rulesPath)
            .configure();
        break;
      case "ip-tables":
        context.getBean(IpTablesConfigurator.class, profileOption, rulesPath)
            .configure();
        break;
      case "files":
        context.getBean(FileRuleGroupConfigurator.class, profileOption, rulesPath)
            .configure();
        break;
      default:
        context.stop();
        context.close();
        return 2;
    }

    context.stop();
    context.close();
    return 0;
  }
}
