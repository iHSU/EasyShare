package security.container.io.test;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import security.container.io.HttpClientUtil;

public class HttpClientUtilTest {

	@Test
	public void testGet() {
		fail("Not yet implemented");
	}

	@Test
	public void testPost() {
		String url = "http://security.ihsu.net:8080/SecurityServer/admit.do?p=login";
		Map<String, String> params = new HashMap<String, String>();
		params.put("user-name", "ihsu");
		params.put("password", "ihsu");
		String res = HttpClientUtil.post(url, params);
		System.out.println(res);
	}

	@Test
	public void testUpload() {
		String url = "http://security.ihsu.net:8080/SecurityServer/datauser.do?action=upload";
		Map<String, String> params = new HashMap<String, String>();
		params.put("owner", "Sky@buaa.edu.cn");
		String path = "D:/develop-env/test/NotifyDemo2/Java注解.pdf.cipher";
		String result = HttpClientUtil.upload(url, params, path);
		System.out.println(result);
	}

}
