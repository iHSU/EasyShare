package security.container.model.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.util.Date;

import org.junit.Test;

import security.container.model.FacetBasic;
import security.container.model.FacetCipher;
import security.container.model.FacetPolicy;
import security.container.model.FacetTrack;
import security.container.model.SecurityData;

public class SecurityDataTest {

	@Test
	public void test() {
		FacetBasic fb = new FacetBasic();
		fb.setSrc("/optd/sfjksfj/df.txt");
		fb.setCreateTime(new Timestamp(new Date().getTime()));
		fb.setLastUpdateTime(new Timestamp(new Date().getTime()));
		fb.setMD5("tss");
		fb.setOwner("owner");
		fb.setSize(123);

		FacetCipher fc = new FacetCipher();
		fc.setDataCipher("cipher".getBytes());
		fc.setKeyCipher("key".getBytes());

		FacetPolicy fp = new FacetPolicy();
		fp.setPolicy("policy".getBytes());
		fp.setSrc("src");

		FacetTrack ft = new FacetTrack();
		ft.setLog("log");
		ft.setOperator("operator");
		ft.setRecordTime(new Timestamp(new Date().getTime()));
		ft.setReservation("reservation");
		ft.setType("type");
		ft.setVersion("v1");

		SecurityData sd = new SecurityData();
		sd.setBasic(fb);
		sd.setCipher(fc);
		sd.setId(1);
		sd.setPolicy(fp);
//		sd.setTrack(ft);

		try {
			FileOutputStream fos = new FileOutputStream("D:/t.obj");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(sd);
			oos.close();

			FileInputStream fis = new FileInputStream("D:/t.obj");
			ObjectInputStream ois = new ObjectInputStream(fis);
			SecurityData data = (SecurityData) ois.readObject();
			ois.close();
			System.out.println(data.getId()+"-"+data.getBasic().getCreateTime());
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

}
