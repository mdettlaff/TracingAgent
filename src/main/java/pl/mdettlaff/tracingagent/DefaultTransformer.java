package pl.mdettlaff.tracingagent;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;

public class DefaultTransformer implements ClassFileTransformer {

	private ClassPool classPool;

	private List<MethodFilter> filters;

	public DefaultTransformer(List<MethodFilter> filters) {
		classPool = ClassPool.getDefault();
		this.filters = filters;
	}

	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		byte[] resultClassfile = null;
		for (MethodFilter filter : filters) {
			if (filter.matchesClassName(className)) {
				try {
					resultClassfile = addTracingToMethods(className, classfileBuffer);
				} catch (IOException | CannotCompileException e) {
					throw new IllegalStateException("Cannot transform class " + className, e);
				}
				break;
			}
		}
		return resultClassfile;
	}

	private byte[] addTracingToMethods(String className, byte[] classfileBuffer) throws IOException, CannotCompileException {
		CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
		for (CtMethod method : ctClass.getDeclaredMethods()) {
			for (MethodFilter filter : filters) {
				if (filter.matchesClassName(className) && filter.matchesMethod(method)) {
					addTracingToMethod(className, method);
					break;
				}
			}
		}
		return ctClass.toBytecode();
	}

	private void addTracingToMethod(String className, CtMethod method) throws CannotCompileException {
		method.addLocalVariable("TracingAgent_methodStartTime", CtClass.longType);
		String methodDescription = className.replaceAll("([^/])[^/]*/", "$1.") + "." + method.getName();
		String codeBefore = "System.out.println(\"perflog - TracingAgent - method " + methodDescription + " started\");\n";
		codeBefore += "TracingAgent_methodStartTime = System.nanoTime();";
		method.insertBefore(codeBefore);
		String codeAfter = "System.out.println(\"perflog - TracingAgent - method " + methodDescription;
		codeAfter += " executed in \" + java.util.concurrent.TimeUnit.MILLISECONDS.convert(System.nanoTime() - ";
		codeAfter += "TracingAgent_methodStartTime, java.util.concurrent.TimeUnit.NANOSECONDS) + \" ms\");";
		method.insertAfter(codeAfter);
	}
}
