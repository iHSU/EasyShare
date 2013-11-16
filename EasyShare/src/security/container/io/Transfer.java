package security.container.io;

public class Transfer {
	private volatile long transferred;
	private long amount;

	/**
	 * @return the transferred
	 */
	public long getTransferred() {
		return transferred;
	}

	/**
	 * @param transferred
	 *            the transferred to set
	 */
	public void setTransferred(long transferred) {
		this.transferred = transferred;
	}

	/**
	 * @return the amount
	 */
	public long getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(long amount) {
		this.amount = amount;
	}
}