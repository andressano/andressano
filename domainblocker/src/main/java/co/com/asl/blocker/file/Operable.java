package co.com.asl.blocker.file;

import co.com.asl.blocker.enums.Operation;
import java.util.Collection;

public interface Operable extends HostListLoader {
  Collection<Operation> validOperations();
}
