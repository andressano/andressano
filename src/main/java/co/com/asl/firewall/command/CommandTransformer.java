package co.com.asl.firewall.command;

import org.apache.commons.collections4.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CommandTransformer implements Transformer<Command, String> {
	private final Logger log = LoggerFactory.getLogger(getClass());

	public synchronized String transform(Command command) {
		try {
			return command.execute();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return "";
	}
}
