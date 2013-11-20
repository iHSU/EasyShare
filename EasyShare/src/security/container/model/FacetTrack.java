package security.container.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class FacetTrack implements Serializable, Comparable<FacetTrack> {
	private static final long serialVersionUID = 1L;
	public static final String TYPE_READ = "R";
	public static final String TYPE_WRITE = "W";
	public static final String TYPE_ACCESS = "A";
	public static final String TYPE_CREATE = "C";
	public static final String OPERATOR_USER = "U";
	public static final String OPERATOR_SERVER = "S";
	
	private String type;
	private Timestamp recordTime;
	private String version;
	private String operator;
	private String log;
	private String reservation;

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the recordTime
	 */
	public Timestamp getRecordTime() {
		return recordTime;
	}

	/**
	 * @param recordTime
	 *            the recordTime to set
	 */
	public void setRecordTime(Timestamp recordTime) {
		this.recordTime = recordTime;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the operator
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * @param operator
	 *            the operator to set
	 */
	public void setOperator(String operator) {
		this.operator = operator;
	}

	/**
	 * @return the log
	 */
	public String getLog() {
		return log;
	}

	/**
	 * @param log
	 *            the log to set
	 */
	public void setLog(String log) {
		this.log = log;
	}

	/**
	 * @return the reservation
	 */
	public String getReservation() {
		return reservation;
	}

	/**
	 * @param reservation
	 *            the reservation to set
	 */
	public void setReservation(String reservation) {
		this.reservation = reservation;
	}

	@Override
	public int compareTo(FacetTrack arg0) {
		
		return this.recordTime.compareTo(arg0.getRecordTime());
	}

}
