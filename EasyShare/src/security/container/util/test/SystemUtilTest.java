package security.container.util.test;

import org.junit.Test;

import security.container.util.SystemUtil;

public class SystemUtilTest {

	@Test
	public void testTest() {
//		SystemUtil.deleteFile("D:\\work\\tmp\\tmp6525969052785279864.sign");
//		String decPath =  "D:\\develop-env\\test\\NotifyDemo2\\mysql.txt";
//		int i = decPath.lastIndexOf("\\");
//		System.out.println(decPath.substring(i+1, decPath.length()));
//		System.out.println("mysql.txt.cipher".substring(0, "mysql.txt.cipher".lastIndexOf(".")));
		SystemUtil.openFile("D:/develop-env/test/NotifyDemo2/mysql.txt");
	}

}
