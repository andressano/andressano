package co.com.asl.firewall.command;

import org.apache.commons.collections4.Closure;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractCommandClosure<T> implements Closure<T> {
	private String commandFormat;
	@Autowired
	private CommandTransformer commandTransformer;

	protected AbstractCommandClosure(String commandFormat) {
		setCommandFormat(commandFormat);
	}

	protected AbstractCommandClosure() {
		setCommandFormat("%s");
	}

	public void execute(T object) {
		String line = String.format(getCommandFormat(), object);
		Command command = new Command(line);
		this.commandTransformer.transform(command);
	}

	public String getCommandFormat() {
		return this.commandFormat;
	}

	protected void setCommandFormat(String commandFormat) {
		this.commandFormat = commandFormat;
	}
}
