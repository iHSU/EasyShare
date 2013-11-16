package security.container.io;

import java.util.HashMap;
import java.util.Map;

public class UserValidate {
	public static boolean validateUser(String id, String passwd) {
		String url = "http://security.ihsu.net:8080/SecurityServer/user.do";
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", id);
		params.put("passwd", passwd);
		String result = HttpClientUtil.post(url, params);
		if (result != null && result.equals(id)) {
			return true;
		}
		return false;
	}
}
