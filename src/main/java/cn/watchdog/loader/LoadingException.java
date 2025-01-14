package cn.watchdog.loader;

/**
 * Runtime exception used if there is a problem during loading
 */
public class LoadingException extends RuntimeException {

	public LoadingException(String message) {
		super(message);
	}

	public LoadingException(String message, Throwable cause) {
		super(message, cause);
	}

}