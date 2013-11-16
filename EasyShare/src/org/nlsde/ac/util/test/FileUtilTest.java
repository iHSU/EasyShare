package org.nlsde.ac.util.test;

import java.io.File;

import org.junit.Test;
import org.nlsde.ac.util.ClientFileUtil;

public class FileUtilTest {

	@Test
	public void testRenameFile() {
		File file = new File(ClientFileUtil.DEFAULT_DIR+"/"+"mysql.text");
		String f = "dddd.txt.cipher";
		System.out.println(file.getName().substring(f.lastIndexOf(".")));
		System.out.println(f.substring(0, f.lastIndexOf(".")));
	}

}
