package pl.mdettlaff.tracingagent;

import java.lang.instrument.Instrumentation;
import java.util.Collections;

public class TracingAgent {

	public static void premain(String agentArgs, Instrumentation inst) {
		System.out.println("starting tracing agent");
		DefaultTransformer transformer = new DefaultTransformer(Collections.<MethodFilter>singletonList(new ControllerFilter()));
		inst.addTransformer(transformer);
	}
}
