package pl.mdettlaff.tracingagent;

import javassist.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.WeakHashMap;

public class TracingTransformer implements ClassFileTransformer {

	private static ClassPool classPool = ClassPool.getDefault();
	private static WeakHashMap loaders = new WeakHashMap<ClassLoader, Object>();

	private List<MethodFilter> filters;

	public TracingTransformer(List<MethodFilter> filters) {
		this.filters = filters;
	}

	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		byte[] resultClassfile = null;
		if (className == null) {
			return null;
		}
		try {
			for (MethodFilter filter : filters) {
				if (filter.matchesClassName(className)) {
					resultClassfile = addTracingToMethods(className, classfileBuffer, loader);
					break;
				}
			}
		} catch (Exception e) {
			System.err.println("TracingAgent - error while transforming class " + className + ": " + e);
			e.printStackTrace();
		}
		return resultClassfile;
	}

	private byte[] addTracingToMethods(String className, byte[] classfileBuffer, ClassLoader loader) throws IOException, CannotCompileException {
		CtClass ctClass = makeClass(classfileBuffer, loader);
		boolean anyMatch = false;
		if (!ctClass.isInterface()) {
			for (CtMethod method : ctClass.getDeclaredMethods()) {
				if (method.isEmpty()) {
					continue;
				}
				for (MethodFilter filter : filters) {
					if (filter.matchesClassName(className) && filter.matchesMethod(method)) {
						addTracingToMethod(className, method);
						anyMatch = true;
						break;
					}
				}
			}
		}
		ctClass.detach();
		return anyMatch ? ctClass.toBytecode() : null;
	}

	private CtClass makeClass(byte[] classfileBuffer, ClassLoader loader) throws IOException {
		if (!loaders.containsKey(loader)) {
			loaders.put(loader, null);
			classPool.appendClassPath(new LoaderClassPath(loader));
		}
		CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
		if (ctClass.isFrozen()) {
			ctClass.defrost();
		}
		return ctClass;
	}

	private void addTracingToMethod(String className, CtMethod method) throws CannotCompileException {
		try {
			method.addLocalVariable("TracingAgent_methodStartTime", CtClass.longType);
			String methodDescription = className.replaceAll("([^/])[^/]*/", "$1.") + "." + method.getName();
			String codeBefore = "System.out.println(\"perflog - TracingAgent - method " + methodDescription + " started\");\n";
			codeBefore += "TracingAgent_methodStartTime = System.nanoTime();";
			method.insertBefore(codeBefore);
			String codeAfter = "System.out.println(\"perflog - TracingAgent - method " + methodDescription;
			codeAfter += " executed in \" + java.util.concurrent.TimeUnit.MILLISECONDS.convert(System.nanoTime() - ";
			codeAfter += "TracingAgent_methodStartTime, java.util.concurrent.TimeUnit.NANOSECONDS) + \" ms\");";
			method.insertAfter(codeAfter);
			System.out.println("TracingAgent - added tracing to method: " + className + "#" + method.getName());
		} catch (Exception e) {
			System.err.println("TracingAgent - error while adding tracing to method: " + className + "#" + method.getName() + ": " + e);
			e.printStackTrace();
		}
	}
}
