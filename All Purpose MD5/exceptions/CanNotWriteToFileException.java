package exceptions;

public class CanNotWriteToFileException extends Exception {

	public CanNotWriteToFileException() {
		super();
	}

	public CanNotWriteToFileException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public CanNotWriteToFileException(String arg0) {
		super(arg0);
	}

	public CanNotWriteToFileException(Throwable arg0) {
		super(arg0);
	}

}
