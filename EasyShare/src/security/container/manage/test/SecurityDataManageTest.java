package security.container.manage.test;

import java.util.List;

import org.junit.Test;

import security.container.manage.SecurityDataManage;
import security.container.model.SecurityData;

public class SecurityDataManageTest {

	@Test
	public void test() {
		SecurityDataManage sdm = new SecurityDataManage("D:/develop-env/test/NotifyDemo2");
		List<SecurityData> sds = sdm.getAllSecurityListDatas();
		for (SecurityData sd : sds) {
			System.out.println(sd.getId()+"-"+sd.getBasic().getName());
		}
	}

}
