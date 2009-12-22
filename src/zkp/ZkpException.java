package zkp;

public class ZkpException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3362985711301696557L;
	
	private String message;

	/**
	 * @param message
	 */
	public ZkpException(String message) {
		super();
		this.message = message;
	}

	
	public ZkpException() {
		super();
		message = "ZKP module created an error";
	}

	public String getMessage() {
		return message;
	}
	
}
