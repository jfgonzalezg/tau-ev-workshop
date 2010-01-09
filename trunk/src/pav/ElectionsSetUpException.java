package pav;

public class ElectionsSetUpException extends Exception {
	private static final long serialVersionUID = -314527972147968081L;
	
	private String message;
	
	public ElectionsSetUpException(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		return message;
	}
	

	
}
