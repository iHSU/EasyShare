package org.nlsde.ac.ui;

import java.awt.BorderLayout;
import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JTextArea;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import security.container.config.Policy;
import security.container.encrypt.Decryptor;
import security.container.encrypt.Encryptor;
import security.container.encrypt.impl.IBE;
import security.container.encrypt.impl.SecurityFactory;
import security.container.manage.FileSystemEvent;
import security.container.model.FacetBasic;
import security.container.model.FacetCipher;
import security.container.model.FacetPolicy;
import security.container.model.FacetTrack;
import security.container.model.SecurityData;
import security.container.util.Constants;
import security.container.util.FileUtil;
import security.container.util.KeyUtil;
import security.container.util.PolicyUtil;
import security.container.util.SystemUtil;

public class UIConsole extends JTextArea implements Observer {
	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(UIConsole.class);

	private UITabPane parent;
	private SecurityFactory factory;
	private String targetSrc;
	private String userID;
	private Map<String, String> handledFiles;	// store the plan file <Plantext, Cipher>

	public UIConsole(UITabPane _parent) {
		this.parent = _parent;
		this.factory = parent.getMainWindow().getSimpleContainer()
				.getSecurityFactory();
		this.targetSrc = parent.getMainWindow().getSimpleContainer().getTargetSrc();
		this.userID = parent.getMainWindow().getSimpleContainer().getId();
		this.handledFiles = new HashMap<String, String>();
	}

	public void initilize() {
		// this.setBorder(BorderFactory.createTitledBorder("System Console"));
		this.setEditable(false);
		// register the console observer to the FileWatcher Observable.
		this.parent.getMainWindow().getSimpleContainer()
				.getSecurityDataManage().getWatcher().addObserver(this);
		
		File dir = new File(this.targetSrc);
		File[] files = dir.listFiles();
		for (File file : files) {
			String name = file.getName();
			if (!name.endsWith(".cipher")) {
				handledFiles.put(file.getName(), file.getName() + ".cipher");
			}
		}
		parent.add(this, BorderLayout.CENTER);
	}
	
	private boolean isHandled(String fileName) {
		return handledFiles.containsKey(fileName) | handledFiles.containsValue(fileName);
	}

	@Override
	public void update(Observable observable, Object fileSystemEvent) {
		FileSystemEvent event = (FileSystemEvent) fileSystemEvent;
		// the trigger to encrypt or decrypt.
		if (event.getEventKind().name().equals("ENTRY_CREATE")) {
			String fileName = event.getFilePath();
			String fileType = fileName.substring(fileName.lastIndexOf("."));
			
			// find a d cipher and auto-decrypt.
			if (fileType.equals(".cipher") && !isHandled(fileName)) {
				decryptProcess(fileName);
			}
			// auto-encrypt
			else if (!fileType.equals(".cipher") && !isHandled(fileName)) {
				encryptProcess(fileName);
			}
		} 
		else if (event.getEventKind().name().equals("ENTRY_DELETE")) {
			String fileName = event.getFilePath();
			String fileType = fileName.substring(fileName.lastIndexOf("."));
			// if user delete the cipher file
			if (fileType.equals(".cipher")) {
				String key = fileName.substring(0, fileName.lastIndexOf("."));
				if(handledFiles.containsKey(key)) {
					handledFiles.remove(key);
				}
			}
			// if user delete the data file 
			else {
				String correspondCipherPath = this.targetSrc + "/" + fileName + ".cipher";
				new File(correspondCipherPath).delete();
				handledFiles.remove(fileName);
			}
		}
		else {
//			this.append("Other events: ");
//			this.append(event.getFilePath() + " has been "
//					+ event.getEventKind().name());
//			this.append("\n");
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			this.paintImmediately(this.getBounds());
//			logger.info(event.getFilePath() + " has been "
//					+ event.getEventKind().name());
		}
	}

	private void encryptProcess(String fileName) {
		// 1. Acquire the default policy file
		Policy defaultPolicy = this.parent.getMainWindow().getSimpleContainer()
				.getConfigurationManage().getDefaultPolicy();
		if (defaultPolicy == null) {
			logger.info("No Default Policy.");
			this.append("没有指定默认加密策略，待加密文件已被移除.\n");
			return ;
		}
		PolicyUtil policyUtil = new PolicyUtil();
		String policyDataFilePath = Constants.TEMP_PATH + "/" + defaultPolicy.getName() + ".dat";
		policyUtil.toFile(policyDataFilePath, defaultPolicy.getContent());
		
		FacetCipher fc = new FacetCipher();
		
		// 2. ABE encrypt -> AES Key && the cipher of AES Key
		Encryptor abeEncrypt = (Encryptor) factory.get(SecurityFactory.ABE_ENCRYPT);
		boolean res = abeEncrypt.encrypt(Constants.TEMP_KEY_AES,
				Constants.TEMP_KEY_AES_CIPHER, policyDataFilePath);
		if(!res) {
			logger.info("ABE Encrypt Failed.");
			this.append("ABE加密失败，待加密文件已被移除.\n");
			return ;
		}
		fc.setKeyCipher(FileUtil.writeToBytes(Constants.TEMP_KEY_AES_CIPHER));
		
		// 3. AES encrypt
		// encrypt the data
		String planPath = this.targetSrc + "/" + fileName;
		String cipherTempPath = SystemUtil.getTempFileName(Constants.WORK_SPACE, ".cipher");
		Encryptor aesEncrypt = (Encryptor) factory.get(SecurityFactory.AES_ENCRYPT);
		res = aesEncrypt.encrypt(planPath, cipherTempPath, Constants.TEMP_KEY_AES);
		if(!res) {
			logger.info("AES Encrypt Failed.");
			this.append("AES加密失败，待加密文件已被移除.\n");
			return ;
		}
		fc.setDataCipher(FileUtil.writeToBytes(cipherTempPath));
		
		// encrypt the read or write track information
		List<FacetTrack> rwTracks = new ArrayList<FacetTrack>();
		FacetTrack readWriteTrack = new FacetTrack();
		readWriteTrack.setLog("文件创建");
		readWriteTrack.setOperator(FacetTrack.OPERATOR_USER + "|" + this.userID);
		readWriteTrack.setRecordTime(new Timestamp(new Date().getTime()));
		readWriteTrack.setType(FacetTrack.TYPE_WRITE);
		readWriteTrack.setReservation("Reservation");
		readWriteTrack.setVersion("Version 1");
		rwTracks.add(readWriteTrack);
		String wtTrackPath = SystemUtil.getTempFileName(Constants.WORK_SPACE, ".track");
		String wtCipherTempPath = SystemUtil.getTempFileName(Constants.WORK_SPACE, ".track.cipher");
		FileUtil.writeObjToFile(rwTracks, wtTrackPath);
		res = aesEncrypt.encrypt(wtTrackPath, wtCipherTempPath, Constants.TEMP_KEY_AES);
		if(!res) {
			logger.info("AES Track Information Encrypt Failed.");
			this.append("AES加密失败，待加密文件已被移除.\n");
			return ;
		}
		fc.setTrackCipher(FileUtil.writeToBytes(wtCipherTempPath));
		FileUtil.deleteFile(wtTrackPath);
		FileUtil.deleteFile(wtCipherTempPath);
		
		// 4. IBE sign
		IBE ibe = (IBE) factory.get(SecurityFactory.IBE);
		final String signFile = SystemUtil.getTempFileName(Constants.WORK_SPACE, ".sign");
		ibe.sign(Constants.KEY_IBE_PRIVATE, cipherTempPath, signFile);
		fc.setSignCipher(FileUtil.writeToBytes(signFile));
		
		// delete the temporary cipher file.
		SystemUtil.deleteFile(cipherTempPath);
		 
		FacetPolicy fp = new FacetPolicy();
		fp.setSrc("");
		fp.setPolicy(defaultPolicy.getContent().getBytes());
		
		FacetBasic fb = new FacetBasic();
		fb.setSrc(planPath);
		fb.setCreateTime(new Timestamp(new Date().getTime()));
		fb.setLastUpdateTime(new Timestamp(new Date().getTime()));
		fb.setName(fileName);
		fb.setOwner(this.userID);
		fb.setMD5(FileUtil.getMD5(planPath));
		fb.setSize(new File(planPath).length());
		
		FacetTrack accessTrack = new FacetTrack();
		accessTrack.setLog("文件创建");
		accessTrack.setOperator(FacetTrack.OPERATOR_USER + "|" + this.userID);
		accessTrack.setRecordTime(new Timestamp(new Date().getTime()));
		accessTrack.setType(FacetTrack.TYPE_CREATE);
		accessTrack.setReservation("Reservation");
		accessTrack.setVersion("Version 1");
		
		SecurityData sd = new SecurityData();
		sd.setPolicy(fp);
		sd.setCipher(fc);
		sd.addAccessTrack(accessTrack);
		sd.setReadWriteTracks(rwTracks);
		sd.setBasic(fb);
		
		String cipherPath = planPath + ".cipher";
		FileUtil.writeObjToFile(sd, cipherPath);
		logger.info("Encrypt Successful.");
		this.append(fileName + ": 文件加密成功.\n");
		this.paintImmediately(this.getBounds());
		
		String cipherFileName = cipherPath.substring(cipherPath.lastIndexOf("/") + 1, cipherPath.length());
		this.append("获得密文为: " + cipherFileName + ".\n");
		this.paintImmediately(this.getBounds());
		
		handledFiles.put(fileName, cipherFileName);
	}

	private void decryptProcess(String fileName) {
		
		this.append("发现一个新的密文: " + fileName + "\n");
		this.paintImmediately(this.getBounds());
		logger.info("Find a new cipher file." + fileName);

		File cipherFile = new File(this.targetSrc + "/" + fileName);
		SecurityData sd = (SecurityData) FileUtil.readObjFromFile(cipherFile);

		this.append("向安全服务器申请ABE私钥\n");
		this.paintImmediately(this.getBounds());
		logger.info("Request the abe key.");
		String url = "http://security.ihsu.net:8080/SecurityServer/key.do?action=key";
		String id = IBE.authorOfKey(Constants.KEY_IBE_PRIVATE);
		String abe_key_pack = KeyUtil.getABEKey(url, id);

		this.append("获取ABE私钥成功\n");
		this.paintImmediately(this.getBounds());
		logger.info("Get the abe key.");
		String[] tmp = KeyUtil.unpack(abe_key_pack);
		String abe_key_ibe_sign = tmp[0];
		String abe_key_ibe_cipher = tmp[1];

		// decrypt abe privacy key cipher
		IBE ibe = (IBE) factory.get(SecurityFactory.IBE);
		boolean flag = ibe.decrypt(Constants.KEY_IBE_PRIVATE,
				Constants.TEMP_KEY_ABE, abe_key_ibe_cipher);
		String publisher = ibe.verify(Constants.TEMP_KEY_ABE,
		 abe_key_ibe_sign);
		logger.info("ABE Key Publish by: " + publisher);

		// decrypt aes key
		if (flag) {
			Decryptor abeDecrypt = (Decryptor) factory
					.get(SecurityFactory.ABE_DECRYPT);
			FileUtil.readBytes(sd.getCipher().getKeyCipher(),
					Constants.TEMP_KEY_AES_CIPHER);
			flag = abeDecrypt.decrypt(Constants.TEMP_KEY_AES_CIPHER,
					Constants.TEMP_KEY_AES_DEC, Constants.TEMP_KEY_ABE);
		}

		if (flag) {
			Decryptor aesDecrypt = (Decryptor) factory
					.get(SecurityFactory.AES_DECRYPT);
			File srcFile = new File(this.targetSrc	+ "/" + fileName);
			String cipherPath = srcFile.getAbsolutePath();
			String decPath = cipherPath.substring(0, cipherPath.indexOf(".cipher"));
			
			String cipherTemp = SystemUtil.getTempFileName(Constants.WORK_SPACE, ".tmp");
			FileUtil.readBytes(sd.getCipher().getDataCipher(), cipherTemp);					
			aesDecrypt.decrypt(cipherTemp, decPath, Constants.TEMP_KEY_AES);
			FileUtil.deleteFile(cipherTemp);
			String decFileName = decPath.substring(decPath.lastIndexOf("\\") + 1, decPath.length());
			handledFiles.put(decFileName, fileName);
			
			// decrypt the read/write track information.
			String trackCipherTemp = SystemUtil.getTempFileName(Constants.WORK_SPACE, ".track.cipher.tmp");
			String trackTemp = SystemUtil.getTempFileName(Constants.WORK_SPACE, ".track.tmp");
			FileUtil.readBytes(sd.getCipher().getTrackCipher(), trackCipherTemp);
			aesDecrypt.decrypt(trackCipherTemp, trackTemp, Constants.TEMP_KEY_AES);
			@SuppressWarnings("unchecked")
			List<FacetTrack> rwTracks = (List<FacetTrack>) FileUtil.readObjFromFile(trackTemp);
			sd.setReadWriteTracks(rwTracks);
			FileUtil.writeObjToFile(sd, srcFile.getAbsolutePath());
			
			FileUtil.deleteFile(trackCipherTemp);
			FileUtil.deleteFile(trackTemp);
		}

		this.append( fileName + " 解密成功.\n");
		this.paintImmediately(this.getBounds());
		logger.info("Decrypted Successfully.");
	}
}
