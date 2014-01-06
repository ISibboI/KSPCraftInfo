package de.isibboi.kspcraftinfo.daemon;

public interface DaemonEventListener {
	/**
	 * 
	 * @param e
	 * @return True if en error occurred.
	 */
	public boolean receiveDaemonEvent(String e);
}