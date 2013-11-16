package security.container.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class DateUtil {
	public static long getDateRandomID() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String now = sdf.format(new Date());
		Random random = new Random();
		String num;
		int i = random.nextInt(1000);
		if (i < 10) {
			num = "00" + i;
		}
		else if (i < 100 && i >=10) {
			num = "0" + i;
		}
		else {
			num = "" + i;
		}
		return Long.parseLong(now + num);
	}
	
	public static String getFormatTime(Timestamp time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(time);
	}
}
