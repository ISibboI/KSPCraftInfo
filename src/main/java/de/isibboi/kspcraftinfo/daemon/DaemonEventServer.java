package de.isibboi.kspcraftinfo.daemon;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.net.TCPSocketManager;

public class DaemonEventServer implements Runnable {
	public static final int PORT = 3294;
	private static final int SOCKET_TIMEOUT = 5000;
	private static final int CONNECT_TIMEOUT = 100;
	private static final int INPUT_BUFFER_SIZE = 64;

	private final Logger log = LogManager.getLogger(getClass());

	private final List<DaemonEventListener> listeners = new ArrayList<>();

	private volatile Thread self;
	private volatile boolean stop;
	private volatile boolean listening;

	private char[] inputBuffer = new char[INPUT_BUFFER_SIZE];

	public void addDaemonEventListener(DaemonEventListener listener) {
		listeners.add(listener);
	}

	public void removeDaemonEventListener(DaemonEventListener listener) {
		listeners.remove(listener);
	}

	public void start() {
		if (self != null) {
			throw new IllegalStateException("Server already started.");
		}

		stop = false;
		self = new Thread(this, getClass().getSimpleName());
		self.start();
	}

	public void stop() {
		if (self == null) {
			throw new IllegalStateException("Server is not running.");
		}

		stop = true;
	}

	@Override
	public void run() {
		ServerSocket server = null;

		try {
			server = new ServerSocket(PORT, 8, InetAddress.getByName("localhost"));
			server.setSoTimeout(CONNECT_TIMEOUT);
		} catch (IOException e) {
			log.fatal("Could not open daemon port", e);
			stop = true;
		}

		while (!stop) {
			Socket client = null;

			try {
				if (stop) {
					listening = false;
				}
				
				client = server.accept();
				client.setTcpNoDelay(true);
				client.setSoTimeout(SOCKET_TIMEOUT);

				String command = readLine(client);
				boolean error = false;

				for (DaemonEventListener listener : listeners) {
					if (listener.receiveDaemonEvent(command)) {
						error = true;
					}
				}

				PrintStream out = new PrintStream(client.getOutputStream());
				out.println(error ? "ER" : "OK");

				client.close();
			} catch (SocketTimeoutException e) {
				if (client != null) {
					try {
						client.close();
					} catch (IOException e1) {
						log.log(Level.ERROR, "Could not close client connection.", e);
					}
				}
			} catch (IOException e) {
				log.log(Level.ERROR, "Could not accept connection.", e);
			}

			if (!stop) {
				listening = true;
			}
		}

		listening = false;
		self = null;
	}

	private String readLine(Socket client) throws IOException {
		InputStreamReader in = new InputStreamReader(client.getInputStream());
		int offset = 0;
		boolean receivedNewline = false;

		do {
			int length = in.read(inputBuffer, offset, inputBuffer.length - offset);

			if (length == -1) {
				return null;
			} else {
				int newOffset = offset;

				for (; newOffset < offset + length && !receivedNewline; newOffset++) {
					if (inputBuffer[newOffset] == '\n') {
						receivedNewline = true;
					}
				}

				offset = newOffset;
			}

			if (offset == inputBuffer.length) {
				receivedNewline = true;
			}
		} while (!receivedNewline);

		if (offset < inputBuffer.length) {
			return String.valueOf(inputBuffer, 0, offset).trim();
		} else {
			return null;
		}
	}
	
	public boolean isListening() {
		return listening;
	}
}