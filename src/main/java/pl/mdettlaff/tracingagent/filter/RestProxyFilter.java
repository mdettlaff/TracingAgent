package pl.mdettlaff.tracingagent.filter;

import javassist.CtMethod;
import pl.mdettlaff.tracingagent.MethodFilter;

public class RestProxyFilter implements MethodFilter {

	@Override
	public boolean matchesClassName(String className) {
		return className.startsWith("pl/mdettlaff/") && className.matches(".*REST.*Proxy$");
	}

	@Override
	public boolean matchesMethod(CtMethod method) {
		return true;
	}
}
