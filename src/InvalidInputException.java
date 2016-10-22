/**
 * 封装了异常类
 * 
 * @author Myths
 *
 */
public class InvalidInputException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidInputException() {

	}

	public InvalidInputException(String msg) {
		super(msg);
	}
}
