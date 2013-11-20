package security.container.util.test;

import org.junit.Test;

public class StringTest {

	@Test
	public void test() {
		String r = "dfsfsf!@#$%jfhdjksfhsj!@#$%hfkdsjfhskfh!@#$%";
		System.out.println(r.substring(0, r.lastIndexOf("!@#$%")));
		double x = (double)132423/223456*100 ; 
		System.out.println((double)(Math.round(x)) + "%");
	}

}
