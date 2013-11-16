package security.container.intercepter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ProxyHandler implements InvocationHandler {
	private Object target;
	TracksInterceptor ti = new TracksInterceptor();

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result = null;
		if (method.getName().equals("info")) {
			// TODO
			result = method.invoke(target, args);
			// TODO
		} else {
			result = method.invoke(target, args);
		}
		return result;

	}

	public void setTarget(Object o) {
		this.target = o;
	}
}