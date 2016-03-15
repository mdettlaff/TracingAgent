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
			boolean hasAnnotation = method.getDeclaringClass().hasAnnotation(Class.forName("javax.annotation.Resource"));
			return hasAnnotation && method.getName().equals("init");
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Cannot filter method " + method.getName(), e);
		}
	}
}
