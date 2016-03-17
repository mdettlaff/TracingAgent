package pl.mdettlaff.tracingagent.filter;

import javassist.CtMethod;
import pl.mdettlaff.tracingagent.MethodFilter;

public class ControllerFilter implements MethodFilter {

	@Override
	public boolean matchesClassName(String className) {
		return className.startsWith("pl/mdettlaff/");
	}

	@Override
	public boolean matchesMethod(CtMethod method) {
		try {
			return method.getName().equals("init") && method.getDeclaringClass().hasAnnotation(Class.forName("javax.annotation.Resource"));
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Cannot filter method " + method.getName(), e);
		}
	}
}
