package co.com.asl.blocker.file.line;

import java.util.function.Predicate;

public class LinePredicates {

    private LinePredicates() {
    }

    public static Predicate<String> isNotComment() {
        return line -> !line.startsWith("#");
    }

    public static Predicate<String> hostPredicate() {
        return l -> l.matches(LineConstants.HOSTNAME_PATTERN);
    }
}
