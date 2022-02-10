package co.com.asl.firewall.command;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class CommandClosure extends AbstractCommandClosure<String> {

	public CommandClosure() {
		super();
	}

	public CommandClosure(String commandFormat) {
		super(commandFormat);
	}
}
