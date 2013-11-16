package security.container.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import security.container.util.DateUtil;

/**
 * Self-Contained Tetrahedral Security Data Model
 * 
 * @author RunhuaXU
 * 
 */
public class SecurityData implements Serializable {
	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(SecurityData.class);
	
	private long id;
	private FacetBasic basic;
	private FacetCipher cipher;
	private FacetPolicy policy;
	private List<FacetTrack> accessTracks;
	private List<FacetTrack> readWriteTracks;
	
	public SecurityData() {
		accessTracks = new ArrayList<FacetTrack>();
		id = DateUtil.getDateRandomID();
		logger.debug("Security Data Init:" + id);
	}
	
	public void addAccessTrack(FacetTrack track) {
		accessTracks.add(track);
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the basic
	 */
	public FacetBasic getBasic() {
		return basic;
	}

	/**
	 * @param basic
	 *            the basic to set
	 */
	public void setBasic(FacetBasic basic) {
		this.basic = basic;
	}

	/**
	 * @return the cipher
	 */
	public FacetCipher getCipher() {
		return cipher;
	}

	/**
	 * @param cipher
	 *            the cipher to set
	 */
	public void setCipher(FacetCipher cipher) {
		this.cipher = cipher;
	}

	/**
	 * @return the policy
	 */
	public FacetPolicy getPolicy() {
		return policy;
	}

	/**
	 * @param policy
	 *            the policy to set
	 */
	public void setPolicy(FacetPolicy policy) {
		this.policy = policy;
	}

	/**
	 * @return the accessTracks
	 */
	public List<FacetTrack> getAccessTracks() {
		return accessTracks;
	}

	/**
	 * @param accessTracks the accessTracks to set
	 */
	public void setAccessTracks(List<FacetTrack> accessTracks) {
		this.accessTracks = accessTracks;
	}

	/**
	 * @return the readWriteTracks
	 */
	public List<FacetTrack> getReadWriteTracks() {
		return readWriteTracks;
	}

	/**
	 * @param readWriteTracks the readWriteTracks to set
	 */
	public void setReadWriteTracks(List<FacetTrack> readWriteTracks) {
		this.readWriteTracks = readWriteTracks;
	}
}
