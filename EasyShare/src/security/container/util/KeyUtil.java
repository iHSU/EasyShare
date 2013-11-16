package security.container.util;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import security.container.io.HttpClientUtil;

public class KeyUtil {
	private static Log logger = LogFactory.getLog(KeyUtil.class);
	
	private static final int ELEMENT_SIZE = 260;
	
	public static String getABEKey(String url, String id) {
		logger.info("the key request id: " + id);
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", id);
		params.put("type", "A");
		
//		List<Pair> attr = getExtendAttributes("");       /////原算法不需要
//		List<String> attr_params = new ArrayList<String>();
//		for(Pair p: attr){                                   	 /////原算法不需要
//			logger.debug("扩展属性: " + p.toString());  		 /////原算法不需要
//			attr_params.add(p.toString());                    	 /////原算法不需要
//		}
//		params.put("attr_params_list", attr_params);
		
		if (HttpClientUtil.get(url, params, "UTF-8", Constants.TEMP_KEY_ABE_PACK)) {
			logger.info("Get ABE-Private-Key Ok!");
			return Constants.TEMP_KEY_ABE_PACK;
		}
		return "";
	}
	
	public static String[] unpack(String packFile){
		String[] tmp = new String[2];
		try {
			DataInputStream in = new DataInputStream(new FileInputStream(packFile));
			int signLen = in.readInt();
			
			byte[] sign = new byte[signLen];
			in.read(sign);
			
			//store sign file
			String signFile = SystemUtil.getTempFileName(Constants.WORK_SPACE, ".sign");
			OutputStream out = new FileOutputStream(signFile);
			out.write(sign);
			out.close();
			logger.debug("upack: sign file: "+ signFile);
			tmp[0] = signFile;
			
			//store cipher 
			String cipherFile = SystemUtil.getTempFileName(Constants.WORK_SPACE, ".cipher");
			out = new FileOutputStream(cipherFile);
			int ch;
			while((ch = in.read()) != -1){
				out.write(ch);
			}
			out.close();
			in.close();
			logger.debug("upack: cipher file: "+ cipherFile);
			tmp[1] = cipherFile;
				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tmp;
	}
	
	private List<Pair> getExtendAttributes(String ctFile) {
		List<Pair> attrs = new ArrayList<Pair>();
		try {
			InputStream in = new FileInputStream(ctFile);
			in.skip(ELEMENT_SIZE * 2 + 4);
			while (in.available() > 0) {
				byte[] bytes = new byte[4];
				in.read(bytes);
				int father = bytesToInt(bytes);
				logger.debug("father: " + father);
				in.read(bytes);
				int nodeType = bytesToInt(bytes);
				logger.debug("nodetype:" + " " + nodeType);
				if (nodeType == -2) {
					in.read(bytes);
					int k = bytesToInt(bytes);
					logger.debug("k:" + k);
					in.read(bytes);
					int num = bytesToInt(bytes);
					logger.debug("num:" + num);
					if (k < 0) {
						Pair pair = new Pair();
						pair.operator = String.valueOf(k);
						logger.debug("operator:" + pair.operator);
						in.skip(20 + ELEMENT_SIZE);
						in.skip(4);
						in.read(bytes);
						int size = bytesToInt(bytes);
						byte[] content = new byte[size];		
						in.read(content);
						pair.left = new String(content);
						logger.debug("left:" + pair.left);
						in.skip(4);
						in.read(bytes);
						size = bytesToInt(bytes);
						content = new byte[size];
						in.read(content);
						pair.right = new String(content);
						logger.debug("right:" + pair.right);

						attrs.add(pair);
					} else {
						in.skip(20 + ELEMENT_SIZE);

					}
				} else if (nodeType == -1) {
					in.read(bytes);
					int k = bytesToInt(bytes);
					in.read(bytes);
					int num = bytesToInt(bytes);
					logger.debug("k:" + k + "\nnum:" + num);
//					in.skip(8);
				}

			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return attrs;
	}
	
	private class Pair {
		String operator;
		String left;
		String right;
		public String toString(){
			return operator + "\t" + left + "\t" + right;
		}
	}
	
	private int bytesToInt(byte[] bytes){
		int v = 0;
		int flag = 1;
		if(bytes[3] < 0){
			flag = -1;
			bytes[3] = (byte)(bytes[3] & 0x7f);
		}
		int factor = 1;
		for(int i = 0; i < 4; i++){
			v += (bytes[i] & 0xff) * factor;
			factor *= 256;
		}
		v *= flag;
		
		return v;
	}
}
