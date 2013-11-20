package security.container.intercepter;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import security.container.encrypt.Encryptor;
import security.container.encrypt.impl.SecurityFactory;
import security.container.io.HttpClientUtil;
import security.container.model.FacetTrack;
import security.container.model.SecurityData;
import security.container.util.Constants;
import security.container.util.FileUtil;
import security.container.util.SystemUtil;

public class TracksInterceptor {
	private static Log logger = LogFactory.getLog(TracksInterceptor.class);

	/**
	 * Before the file upload, remove the Read/Write Tracks.
	 * 
	 * @param filePath
	 * @return
	 */
	public String clearTracksAndKeys(String filePath) {
		// 共享文件前，清空Read, Write追踪记录
		logger.debug("get the new type of upload file.");
		SecurityData sd = (SecurityData) FileUtil.readObjFromFile(filePath);
		sd.setReadWriteTracks(null);
		sd.setAesKey(null);
		String filename = filePath.substring(filePath.lastIndexOf("/") + 1,
				filePath.length());
		File temp = new File(Constants.TEMP_PATH + "/" + filename);
		try {
			temp.createNewFile();
			FileUtil.writeObjToFile(sd, temp.getAbsolutePath());
			return temp.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * After the file upload, delete the temper file.
	 * 
	 * @param filePath
	 * @return
	 */
	public void afterUpload(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
		logger.debug("the upload temper file has been deleted.");
	}

	/**
	 * 数据安全解密后的，读追踪信息的记录
	 * 
	 * @param fileName
	 *            密文文件
	 * @param userID
	 *            用户ID
	 * @param fileDir
	 *            密文文件夹
	 */
	public void updateAndSynchronizeReadTracks(String filePath, String userID,
			SecurityFactory factory) {
		SecurityData sd = (SecurityData) FileUtil.readObjFromFile(filePath);
		List<FacetTrack> rwTracks = sd.getReadWriteTracks();

		FacetTrack readTrack = new FacetTrack();
		readTrack.setLog(userID + " 读取了文件");
		readTrack.setOperator(FacetTrack.OPERATOR_USER + "|" + userID);
		readTrack.setRecordTime(new Timestamp(new Date().getTime()));
		readTrack.setReservation("");
		readTrack.setType(FacetTrack.TYPE_READ);
		readTrack.setVersion(getLastVersion(rwTracks));
		rwTracks.add(readTrack);

		// Get the AES Key.
		String tempAesKeyPath = SystemUtil.getTempFileName(Constants.TEMP_PATH,
				".aes.key");
		FileUtil.readBytes(sd.getAesKey(), tempAesKeyPath);

		String wtTrackPath = SystemUtil.getTempFileName(Constants.TEMP_PATH,
				".track");
		String wtCipherTempPath = SystemUtil.getTempFileName(
				Constants.TEMP_PATH, ".track.cipher");
		FileUtil.writeObjToFile(rwTracks, wtTrackPath);
		Encryptor aesEncrypt = (Encryptor) factory
				.get(SecurityFactory.AES_ENCRYPT);
		boolean res = aesEncrypt.encrypt(wtTrackPath, wtCipherTempPath,
				tempAesKeyPath);
		if (!res) {
			logger.info("AES Track Information Encrypt Failed.");
			return;
		}
		sd.getCipher().setTrackCipher(FileUtil.writeToBytes(wtCipherTempPath));
		FileUtil.deleteFile(tempAesKeyPath);
		FileUtil.deleteFile(wtTrackPath);
		FileUtil.deleteFile(wtCipherTempPath);
		FileUtil.writeObjToFile(sd, filePath);

		// Set the aes key and read/write record to be null
		// before data outsourced.
		String update_path = clearTracksAndKeys(filePath);

		// Synchronize to the data center.
		String url = "http://security.ihsu.net:8080/SecurityServer/datauser.do?action=update";
		Map<String, String> params = new HashMap<String, String>();
		HttpClientUtil.upload(url, params, update_path);
	}

	private String getLastVersion(List<FacetTrack> rwTracks) {
		FacetTrack ft = rwTracks.get(0);
		long time = ft.getRecordTime().getTime();
		for (FacetTrack temp : rwTracks) {
			Timestamp ts = temp.getRecordTime();
			if (ts.getTime() >= time) {
				time = ts.getTime();
				ft = temp;
			}
		}
		return ft.getVersion();
	}

	public void updateWriteTracks(String filePath, String userID,
			SecurityFactory factory) {
		SecurityData sd = (SecurityData) FileUtil.readObjFromFile(filePath);
		List<FacetTrack> rwTracks = sd.getReadWriteTracks();

		FacetTrack readTrack = new FacetTrack();
		readTrack.setLog(userID + " 更新了文件");
		readTrack.setOperator(FacetTrack.OPERATOR_USER + "|" + userID);
		readTrack.setRecordTime(new Timestamp(new Date().getTime()));
		readTrack.setReservation("");
		readTrack.setType(FacetTrack.TYPE_WRITE);
		String lastVersion = getLastVersion(rwTracks); // Version Sample:
														// "Version 3"
		int newVersionNum = Integer.parseInt(lastVersion.split(" ")[1]) + 1;
		readTrack.setVersion("Version " + newVersionNum);
		rwTracks.add(readTrack);

		// Get the AES Key.
		String tempAesKeyPath = SystemUtil.getTempFileName(Constants.TEMP_PATH,
				".aes.key");
		FileUtil.readBytes(sd.getAesKey(), tempAesKeyPath);

		String wtTrackPath = SystemUtil.getTempFileName(Constants.TEMP_PATH,
				".track");
		String wtCipherTempPath = SystemUtil.getTempFileName(
				Constants.TEMP_PATH, ".track.cipher");
		FileUtil.writeObjToFile(rwTracks, wtTrackPath);
		Encryptor aesEncrypt = (Encryptor) factory
				.get(SecurityFactory.AES_ENCRYPT);
		boolean res = aesEncrypt.encrypt(wtTrackPath, wtCipherTempPath,
				tempAesKeyPath);
		if (!res) {
			logger.info("AES Track Information Encrypt Failed.");
			return;
		}
		sd.getCipher().setTrackCipher(FileUtil.writeToBytes(wtCipherTempPath));
		FileUtil.deleteFile(tempAesKeyPath);
		FileUtil.deleteFile(wtTrackPath);
		FileUtil.deleteFile(wtCipherTempPath);
		FileUtil.writeObjToFile(sd, filePath);
	}
}
