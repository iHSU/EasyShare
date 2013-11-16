package security.container.io;

import java.util.Map;

import org.junit.Test;

import security.container.model.CloudData;

public class DataIOTest {

	@Test
	public void test() {
//		Map<Long, String> m = DataIO.getCloudDataNameList();
//		for (Long key : m.keySet()) {
//			System.out.println(key + m.get(key));
//		}
		
		Map<Long, CloudData> m = DataIO.getCloudDatas();
		for (Long key : m.keySet()) {
			System.out.println(key + m.get(key).getStringCreateTime());
		}
	}

}
