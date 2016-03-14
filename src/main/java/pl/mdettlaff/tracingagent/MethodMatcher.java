package pl.mdettlaff.tracingagent;

import javassist.CtMethod;

public interface MethodMatcher {

	boolean matchesClassName(String className);

	boolean matchesMethod(CtMethod method);
}
