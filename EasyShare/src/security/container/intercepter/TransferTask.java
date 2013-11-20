package security.container.intercepter;

public interface TransferTask {
	public void startUploadTask(String path);
	public void startUpdateTask(String path);
	public void startDownloadTask();
}
