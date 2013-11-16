package security.container.manage;

import java.io.File;
import java.io.FilenameFilter;

public class SecurityFileFilter implements FilenameFilter {

	public boolean isCipher(String file) {
		if (file.toLowerCase().endsWith(".cipher")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean accept(File dir, String fname) {
		return isCipher(fname);

	}

}