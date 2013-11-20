package security.container.intercepter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class ProxyFactory {
	public static Object getProxy(Object object, InvocationHandler handler) {
		// 第一个参数是用来创建动态代理的ClassLoader对象，只要该对象能访问Dog接口即可
		// 第二个参数是接口数组，正是代理该接口的数组
		// 第三个参数是代理包含的处理实例
		return Proxy.newProxyInstance(object.getClass().getClassLoader(), object
				.getClass().getInterfaces(), handler);
	}
	
	public static void main(String[] args) {
//		TransferTask task = new Task();
//		TracksProxyHandler handler = new TracksProxyHandler(task);
//		TransferTask proxy = (TransferTask) ProxyFactory.getProxy(task, handler);
//		proxy.startUploadTask("this is a path.");
	}
}