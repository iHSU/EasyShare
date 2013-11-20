package org.nlsde.ac.service.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import security.container.io.HttpClientUtil;
import security.container.io.Transfer;

public class DownloadTaskTest {

	@Test
	public void test() {
		String url = "http://security.ihsu.net:8080/SecurityServer/datauser.do?action=download";
		Map<String, String> params = new HashMap<String, String>();
		params.put("fileName", "mysql.txt.cipher");
		int transferred = 0;
		int amount = 0;
		HttpClientUtil.download(url, params, "UTF-8", new Transfer(), "d:/mysql.txt.cipher");
		System.out.println(transferred+"-"+amount);
	}

}
