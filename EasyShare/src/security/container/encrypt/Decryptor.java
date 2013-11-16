package security.container.encrypt;

public interface Decryptor {
	public boolean decrypt(String cipherPath, String decPath, String keyPath);
}
