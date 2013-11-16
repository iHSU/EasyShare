package security.container.io;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import security.container.model.CloudData;

public class DataIO {

	private final static Log logger = LogFactory.getLog(DataIO.class);

	public static Map<Long, String> getCloudDataNameList() {
		Map<Long, String> map = new HashMap<Long, String>();

		String url = "http://security.ihsu.net:8080/SecurityServer/datauser.do?action=queryList";
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", "nameList");
		String res = HttpClientUtil.get(url, params, "UTF-8");

		JSONObject json = JSONObject.fromObject(res);
		@SuppressWarnings("unchecked")
		Set<String> keys = json.keySet();
		for (String key : keys) {
			String o = (String) json.get(key);
			map.put(Long.parseLong(key), o);
		}
		return map;
	}

	public static Map<Long, CloudData> getCloudDatas() {
		Map<Long, CloudData> map = new HashMap<Long, CloudData>();

		String url = "http://security.ihsu.net:8080/SecurityServer/datauser.do?action=queryList";
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", "data");
		String res = HttpClientUtil.get(url, params, "UTF-8");

		JSONObject json = JSONObject.fromObject(res);
		logger.debug(json);

		@SuppressWarnings("unchecked")
		Set<String> keys = json.keySet();
		for (String key : keys) {
			logger.debug(JSONObject.fromObject(json.get(key)));
			CloudData value = (CloudData) JSONObject.toBean(
					(JSONObject) json.get(key), CloudData.class);
			map.put(Long.parseLong(key), value);
		}
		return map;
	}
}
