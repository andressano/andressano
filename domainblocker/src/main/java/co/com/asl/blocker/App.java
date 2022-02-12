package co.com.asl.blocker;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import co.com.asl.blocker.controller.AntiADSController;
import co.com.asl.blocker.enums.Operation;

public class App {
	public static void main(String[] args) {
		Operation operation = Operation.INVALID_OPERATION;
		try {
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
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath*:META-INF/spring/**/*.xml", "classpath*:META-INF/antiads/**/*.xml");
		context.start();
		AntiADSController antiADSController = context.getBean(AntiADSController.class);
		antiADSController.procesar(new File(line.getOptionValue("file")), operation);
		context.close();
	}

	private static final Options createOptions() {
		Options options = new Options();
		OptionBuilder.withArgName("hostsFile");
		OptionBuilder.hasArg();
		OptionBuilder.isRequired(true);
		OptionBuilder.withDescription("Archivo de hosts.");
		options.addOption(OptionBuilder.create("file"));
		options.addOption("disable", false, "Crea archivo de hosts por defecto.");
		options.addOption("enable", false, "Crea archivo de hosts.");
		options.addOption("nolog", false, "Crea archivo de hosts.");
		return options;
	}
}
