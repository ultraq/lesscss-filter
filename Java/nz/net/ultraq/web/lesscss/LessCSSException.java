
package nz.net.ultraq.web.lesscss;

/**
 * Exception thrown by the LessCSS filter/processor.
 * 
 * @author Emanuel Rabina
 */
public class LessCSSException extends RuntimeException {

	/**
	 * Constructor, set the exception message.
	 * 
	 * @param message
	 */
	LessCSSException(String message) {

		super(message);
	}

	/**
	 * Constructor, set the exception message and cause.
	 * 
	 * @param message
	 * @param cause
	 */
	LessCSSException(String message, Throwable cause) {

		super(message, cause);
	}
}
