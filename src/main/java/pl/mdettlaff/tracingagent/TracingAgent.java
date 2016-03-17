package pl.mdettlaff.tracingagent;

import pl.mdettlaff.tracingagent.filter.ControllerFilter;
import pl.mdettlaff.tracingagent.filter.RestProxyFilter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

public class TracingAgent {

	public static void premain(String agentArgs, Instrumentation inst) {
		System.out.println("TracingAgent - starting");
		ClassFileTransformer transformer = createTransformer();
		inst.addTransformer(transformer);
		System.out.println("TracingAgent - added transformer");
	}

	private static ClassFileTransformer createTransformer() {
		List<MethodFilter> filters = new ArrayList<>();
		filters.add(new ControllerFilter());
		filters.add(new RestProxyFilter());
		return new TracingTransformer(filters);
	}
}
