package de.isibboi.kspcraftinfo.cli;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.isibboi.kspcraftinfo.daemon.DaemonEventListener;
import de.isibboi.kspcraftinfo.daemon.DaemonEventServer;

public class CLITest implements DaemonEventListener {
	private String event;

	@Test
	public void testCLI() {
		DaemonEventServer server = new DaemonEventServer();
		server.start();

		while (!server.isListening()) {
			Thread.yield();
		}
		
		String command = "shutdown";
		server.addDaemonEventListener(this);

		CLI.main(new String[] { command });
		
		server.stop();
		
		assertEquals("Did not receive correct command.", command, event);
	}

	@Override
	public boolean receiveDaemonEvent(String e) {
		event = e;
		return false;
	}
}