package de.isibboi.kspcraftinfo.db;

@SuppressWarnings("serial")
public class DatabaseException extends RuntimeException {
	public DatabaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public DatabaseException(String message) {
		super(message);
	}
}