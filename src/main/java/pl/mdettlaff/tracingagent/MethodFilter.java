package pl.mdettlaff.tracingagent;

import javassist.CtMethod;

public interface MethodFilter {

	boolean matchesClassName(String className);

	boolean matchesMethod(CtMethod method);
}
