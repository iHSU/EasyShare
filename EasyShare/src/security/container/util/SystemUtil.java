package security.container.util;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SystemUtil {
	private static Log logger = LogFactory.getLog(SystemUtil.class);

	public static boolean execute(String command) {
		Process process = null;
		logger.debug(command);
		try {
			process = Runtime.getRuntime().exec(command);

			final InputStream is1 = process.getInputStream();
			final InputStream is2 = process.getErrorStream();
			new Thread() {
				public void run() {
					BufferedReader br1 = new BufferedReader(
							new InputStreamReader(is1));
					try {
						String line1 = null;
						while ((line1 = br1.readLine()) != null) {
							if (line1 != null) {
								logger.debug(line1);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							is1.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();

			new Thread() {
				public void run() {
					BufferedReader br2 = new BufferedReader(
							new InputStreamReader(is2));
					try {
						String line2 = null;
						while ((line2 = br2.readLine()) != null) {
							if (line2 != null) {
								logger.error(line2);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							is2.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();

			process.waitFor();
			process.destroy();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String getTempFileName(String workDir,String suffix) {
		try {
			File f = File.createTempFile("tmp", suffix, new File(workDir + "/tmp/"));
			// f.deleteOnExit();
			return f.getAbsolutePath();
		} catch (IOException e) {
			return "";
		}
	}
	
	public static void deleteFile(String filePath) {
		execute("cmd /c del " + filePath + " /q");
	}
	
	public static void executeByCMD(String command) {
		execute("cmd /c " + command);
	}
	
	public static boolean openFile(String filePath) {
		Desktop desk = Desktop.getDesktop();
		File file = new File(filePath);
		if (!file.exists()) {
			return false;
		}
		try {
			desk.open(file);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
