package exceptions;

public class InvalidPropertyException extends Exception {

	private String extraInformation = null;
	
	public InvalidPropertyException() {
	}

	public InvalidPropertyException(String message) {
		super(message);
	}

	public InvalidPropertyException(Throwable cause) {
		super(cause);
	}

	public InvalidPropertyException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public void setExtraInformation(String message){
		extraInformation = message;
	}
	
	public String getExtraInformation(){
		return extraInformation;
	}


}
