package pl.mdettlaff.tracingagent;

import pl.mdettlaff.tracingagent.filter.ControllerFilter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

public class TracingAgent {

	public static void premain(String agentArgs, Instrumentation inst) {
		System.out.println("starting tracing agent");
		ClassFileTransformer transformer = createTransformer();
		inst.addTransformer(transformer);
	}

	private static ClassFileTransformer createTransformer() {
		List<MethodFilter> filters = new ArrayList<>();
		filters.add(new ControllerFilter());
		return new TracingTransformer(filters);
	}
}
