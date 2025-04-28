package co.com.asl.firewall;

import co.com.asl.firewall.configuration.Config;
import co.com.asl.firewall.configuration.file.FileRuleGroupConfigurator;
import co.com.asl.firewall.configuration.iptables.IpTablesConfigurator;
import co.com.asl.firewall.configuration.nftables.NfTablesConfigurator;
import co.com.asl.firewall.configuration.ufw.UFWConfigurator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Visibility;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(name = "firewall")
public class Main implements Callable<Integer> {

  @Option(names = {"-f",
      "--firewall"}, paramLabel = "ufw|ip-tables|file", description = "Kind of firewall rules", showDefaultValue = Visibility.ALWAYS, defaultValue = "ufw")
  private String firewallOption;

  @Option(names = {"-p",
      "--profile"}, paramLabel = "<profile>", description = "Profile name", showDefaultValue = Visibility.ALWAYS, defaultValue = "default")
  private String profileOption;

  @Option(names = {"-ll",
      "--log-level"}, description = "Log level for logger", defaultValue = "info")
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
    context.registerBean("profileOption", String.class, profileOption);
    context.registerBean("rulesPath", String.class, rulesPath);
    context.start();

    switch (firewallOption) {
      case "ufw":
        context.getBean(UFWConfigurator.class).configure();
        break;
      case "iptables":
        context.getBean(IpTablesConfigurator.class).configure();
        break;
      case "nftables":
        context.getBean(NfTablesConfigurator.class).configure();
        break;
      case "files":
        context.getBean(FileRuleGroupConfigurator.class).configure();
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
