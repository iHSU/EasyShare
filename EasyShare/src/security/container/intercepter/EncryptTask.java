package security.container.intercepter;

public interface EncryptTask {
	public void encryptProcess(String fileName);
	public boolean decryptProcess(String fileName);
}
