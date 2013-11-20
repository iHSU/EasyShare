package security.container.encrypt.impl.test;

import java.io.IOException;

import org.junit.Test;

import security.container.encrypt.Decryptor;
import security.container.encrypt.impl.IBE;
import security.container.encrypt.impl.SecurityFactory;
import security.container.model.SecurityData;
import security.container.util.Constants;
import security.container.util.FileUtil;
import security.container.util.KeyUtil;

public class SecurityFactoryTest {
	@SuppressWarnings("unused")
	@Test
	public void test() throws IOException {

//		String policyFile = "D:/work/test/cs.xml";
		SecurityFactory factory = SecurityFactory
				.getInstance(Constants.WORK_SPACE, "Sky@buaa.edu.cn");
//		PolicyUtil policyUtil = new PolicyUtil(policyFile);
//		String policyDataFilePath = Constants.TEMP_PATH + "/cs.policy.dat";
//		policyUtil.toFile(policyDataFilePath);
//
//		IBE ibe = (IBE) factory.get(SecurityFactory.IBE);
//		final String signFile = SystemUtil.getTempFileName(
//				Constants.WORK_SPACE, ".sign");
//		ibe.sign(Constants.KEY_IBE_PRIVATE,
//				"D:/work/test/extend.xml", signFile);
//
//		FacetCipher fc = new FacetCipher();
//		fc.setSignCipher(FileUtil.writeToBytes(signFile));
//
//		Encryptor abeEncrypt = (Encryptor) factory
//				.get(SecurityFactory.ABE_ENCRYPT);
//		abeEncrypt.encrypt(Constants.TEMP_KEY_AES,
//				Constants.TEMP_KEY_AES_CIPHER, policyDataFilePath);
//		fc.setKeyCipher(FileUtil.writeToBytes(Constants.TEMP_KEY_AES_CIPHER));

//		String planPath = "D:/develop-env/test/extend.xml";
//		String cipherPath = "D:/develop-env/test/extend.xml.cipher";
//		Encryptor aesEncrypt = (Encryptor) factory
//				.get(SecurityFactory.AES_ENCRYPT);
//		aesEncrypt.encrypt(planPath, cipherPath, Constants.TEMP_KEY_AES);
//		fc.setCipher(FileUtil.writeToBytes(cipherPath));
//
//		FacetPolicy fp = new FacetPolicy();
//		fp.setSrc(policyFile);
//		fp.setPolicy(FileUtil.writeToBytes(policyFile));
//		
//		FacetBasic fb = new FacetBasic();
//		fb.setSrc(planPath);
//		fb.setCreateTime(new Timestamp(new Date().getTime()));
//		fb.setLastUpdateTime(new Timestamp(new Date().getTime()));
//		fb.setName("extend.xml");
//		fb.setOwner("iHSU");
//		
//		FacetTrack ft = new FacetTrack();
//		
//		SecurityData sd = new SecurityData();
//		sd.setId(1);
//		sd.setPolicy(fp);
//		sd.setCipher(fc);
//		sd.setTrack(ft);
//		sd.setBasic(fb);
//		
//		FileUtil.writeObjToFile(sd, "D:/work/test/extend.xml.cipher");
		
		String cipherPath = "D:/develop-env/test/extend.xml.cipher";
		SecurityData sd = (SecurityData) FileUtil.readObjFromFile(cipherPath);

//		final String signFile2 = SystemUtil.getTempFileName(
//				Constants.WORK_SPACE, ".sign");
//		FileUtil.readBytes(fc.getSignCipher(), signFile2);
//
		String url = "http://security.ihsu.net:8080/SecurityServer/key.do?action=key";
		String id = IBE.authorOfKey(Constants.KEY_IBE_PRIVATE);
		String abe_key_pack = KeyUtil.getABEKey(url, id);

		String[] tmp = KeyUtil.unpack(abe_key_pack);
		String abe_key_ibe_sign = tmp[0];
		String abe_key_ibe_cipher = tmp[1];

		// decrypt abe privacy key cipher
		IBE ibe = (IBE) factory.get(SecurityFactory.IBE);
		boolean flag = ibe.decrypt(Constants.KEY_IBE_PRIVATE,
				Constants.TEMP_KEY_ABE, abe_key_ibe_cipher);
		// String publisher = ibe.verify(Constants.TEMP_KEY_ABE,
		// abe_key_ibe_sign);
		// System.out.println("ABE Key Publish by: " + publisher);

		// decrypt aes key
		if (flag) {
			Decryptor abeDecrypt = (Decryptor) factory
					.get(SecurityFactory.ABE_DECRYPT);
			FileUtil.readBytes(sd.getCipher().getKeyCipher(), Constants.TEMP_KEY_AES_CIPHER);
			flag = abeDecrypt.decrypt(Constants.TEMP_KEY_AES_CIPHER,
					Constants.TEMP_KEY_AES_DEC, Constants.TEMP_KEY_ABE);
		}

		if (flag) {
			Decryptor aesDecrypt = (Decryptor) factory
					.get(SecurityFactory.AES_DECRYPT);
			String decPath = "D:/develop-env/test/extend.xml";
			String cipher = "D:/develop-env/test/extend.xml.tmp";
			FileUtil.readBytes(sd.getCipher().getDataCipher(), cipher);
			aesDecrypt.decrypt(cipher, decPath, Constants.TEMP_KEY_AES);
		}
		
	}

}
