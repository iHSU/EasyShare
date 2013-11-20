package security.container.intercepter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import security.container.encrypt.impl.SecurityFactory;

public class TracksProxyHandler implements InvocationHandler {
	private Object target;
	private TracksInterceptor interceptor;
	private String userID;
	private String fileDir;
	private SecurityFactory factory;
	
	public TracksProxyHandler(Object target, String userID, String fileDir, SecurityFactory factory) {
		this.target = target;
		this.interceptor = new TracksInterceptor();
		this.userID = userID;
		this.fileDir = fileDir;
		this.factory = factory;
	}
	
	public TracksProxyHandler(Object target, String userID, String fileDir) {
		this.target = target;
		this.interceptor = new TracksInterceptor();
		this.userID = userID;
		this.fileDir = fileDir;
		this.factory = null;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result = null;
		if (method.getName().equals("startUploadTask")) {
			// before the file upload
			// get the temper file without the plain text info. of read/write tracks
			String uploadPath = interceptor.clearTracksAndKeys((String)args[0]);
			// use the new file to upload.
			result = method.invoke(target, new Object[]{uploadPath});
			// after the upload, delete temper file.
			// ti.afterUpload(uploadPath);
		} else if (method.getName().equals("startDownloadTask")) {
			// TODO
			result = method.invoke(target, args);
		} else if (method.getName().equals("decryptProcess")) {
			result = method.invoke(target, args);
			boolean flag = (boolean) result;
			if (flag) { // 解密成功，更新读写记录
				interceptor.updateAndSynchronizeReadTracks(fileDir + "/" + (String)args[0], userID, factory);
			}
		} else if (method.getName().equals("startUpdateTask")) {
			interceptor.updateWriteTracks((String)args[0], userID, factory);
			String uploadPath = interceptor.clearTracksAndKeys((String)args[0]);
			result = method.invoke(target, new Object[]{uploadPath});
		}
		return result;
	}

	public void setTarget(Object o) {
		this.target = o;
	}
}