package de.isibboi.kspcraftinfo.cli;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;

import de.isibboi.kspcraftinfo.daemon.DaemonEventServer;

public class CLI {
	private static final int SOCKET_TIMEOUT = 5000;

	public static void main(String[] args) {
		if (args.length == 0) {
			error("Not enough arguments: 0");
		}

		Set<String> commandList = new HashSet<>();
		commandList.add("shutdown");

		if (commandList.contains(args[0])) {
			if (send(args[0])) {
				System.out.println("SUCCESS!");
			} else {
				System.out.println("ERROR!");
			}
		} else {
			printHelp();
		}
	}

	private static boolean send(String command) {
		try (Socket client = new Socket();) {
			client.setSoTimeout(SOCKET_TIMEOUT);
			SocketAddress address = new InetSocketAddress(InetAddress.getByName("localhost"), DaemonEventServer.PORT);
			client.connect(address, SOCKET_TIMEOUT);

			PrintStream out = new PrintStream(client.getOutputStream());
			out.println(command);

			Reader in = new InputStreamReader(client.getInputStream());
			String result = readChars(in);

			client.close();

			if (result.equals("OK")) {
				return true;
			} else if (result.equals("ER")) {
				return false;
			} else {
				error("Received unknown answer: " + result);
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			error("Could not communicate with daemon.");
			return false;
		}
	}

	private static String readChars(Reader in) throws IOException {
		char[] result = new char[2];

		int character = in.read();

		if (character == -1) {
			return null;
		} else {
			result[0] = (char) character;
		}

		character = in.read();

		if (character == -1) {
			return null;
		} else {
			result[1] = (char) character;
		}

		return String.valueOf(result);
	}

	private static void printHelp() {
		System.out.println("List of commands:");
		System.out.println("help - Print this message.");
		System.out.println("shutdown - Gracefully shutdown daemon.");
	}

	private static void error(String error) {
		System.out.println("Error: " + error);
		System.exit(-1);
	}
}