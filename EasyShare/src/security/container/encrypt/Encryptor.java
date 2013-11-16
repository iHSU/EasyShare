package security.container.encrypt;

public interface Encryptor {
	public boolean encrypt(String plantextPath, String cipherPath, String keyOrPolicyPath);
}
