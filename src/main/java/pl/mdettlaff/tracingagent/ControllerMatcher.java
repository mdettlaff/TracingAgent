package pl.mdettlaff.tracingagent;

import javassist.CtMethod;

public class ControllerMatcher implements MethodMatcher {

	@Override
	public boolean matchesClassName(String className) {
		return className.endsWith("Controller");
	}

	@Override
	public boolean matchesMethod(CtMethod method) {
		return method.getName().equals("init");
	}
}
