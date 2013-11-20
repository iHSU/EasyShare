package security.container.intercepter.test;

import security.container.intercepter.TransferTask;

public class Task implements TransferTask {

	@Override
	public void startUploadTask(String path) {
		System.out.println("ddd-" + path);
	}

	@Override
	public void startDownloadTask() {
		// TODO Auto-generated method stub

	}

	@Override
	public void startUpdateTask(String path) {
		// TODO Auto-generated method stub
		
	}

}
