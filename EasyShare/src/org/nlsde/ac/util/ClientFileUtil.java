package org.nlsde.ac.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class ClientFileUtil {
	
	public static String DEFAULT_DIR = "D:/develop-env/test/NotifyDemo";
	
	public static File createFile(String dir) {
		File file = new File(dir);
		return file;
	}
	
	public static String createCipherFileName(String dir, String origin) {
		File tmp = new File(origin);
		return dir + "\\" +  tmp.getName() + ".cipher";
	}
	
	@SuppressWarnings("resource")
	public static String getAccessLogs(File file) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			StringBuffer logs = new StringBuffer();
			boolean flag = false;
			while (line != null) {
				if (flag) {
					logs.append(line + "\n");
				}
				if (line.equals("****")) {
					flag = true;
				}
				line = reader.readLine();
			}
			return logs.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static boolean renameFile(File origin, String newName) {
		return origin.renameTo(new File(DEFAULT_DIR + "/" + newName));
	}
	
	/**
	 * 复制文件
	 * 
	 * @param srcFile
	 *            源文件File
	 * @param destDir
	 *            目标目录File
	 * @param newFileName
	 *            新文件名
	 * @return 实际复制的字节数，如果文件、目录不存在、文件为null或者发生IO异常，返回-1
	 */
	public static long copyFile1(File srcFile, File destDir, String newFileName) {
		long copySizes = 0;
		if (!srcFile.exists()) {
			System.out.println("源文件不存在");
			copySizes = -1;
		} else if (!destDir.exists()) {
			System.out.println("目标目录不存在");
			copySizes = -1;
		} else if (newFileName == null) {
			System.out.println("文件名为null");
			copySizes = -1;
		} else {
			try {
				BufferedInputStream bin = new BufferedInputStream(
						new FileInputStream(srcFile));
				BufferedOutputStream bout = new BufferedOutputStream(
						new FileOutputStream(new File(destDir, newFileName)));
				int b = 0, i = 0;
				while ((b = bin.read()) != -1) {
					bout.write(b);
					i++;
				}
				bout.flush();
				bin.close();
				bout.close();
				copySizes = i;

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return copySizes;
	}

	/**
	 * 复制文件(以超快的速度复制文件)
	 * 
	 * @param srcFile
	 *            源文件File
	 * @param destDir
	 *            目标目录File
	 * @param newFileName
	 *            新文件名
	 * @return 实际复制的字节数，如果文件、目录不存在、文件为null或者发生IO异常，返回-1
	 */
	@SuppressWarnings("resource")
	public static long copyFile2(File srcFile, File destDir, String newFileName) {
		long copySizes = 0;
		if (!srcFile.exists()) {
			System.out.println("源文件不存在");
			copySizes = -1;
		} else if (!destDir.exists()) {
			System.out.println("目标目录不存在");
			copySizes = -1;
		} else if (newFileName == null) {
			System.out.println("文件名为null");
			copySizes = -1;
		} else {
			try {
				FileChannel fcin = new FileInputStream(srcFile).getChannel();
				FileChannel fcout = new FileOutputStream(new File(destDir,
						newFileName)).getChannel();
				long size = fcin.size();
				fcin.transferTo(0, fcin.size(), fcout);
				fcin.close();
				fcout.close();
				copySizes = size;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return copySizes;
	}

}
