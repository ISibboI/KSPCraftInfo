package de.isibboi.kspcraftinfo.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.sql.PreparedStatement;
import java.util.Iterator;

public class ModDatabase {
	private static final int BATCH_SIZE = 100;

	private static final Logger LOG = LogManager.getLogger(ModDatabase.class);
	private static final ModDatabase INSTANCE = new ModDatabase();

	private ModDatabase() {
		try {
			Properties p = new Properties();
			InputStream in = getClass().getResourceAsStream(".password");
			p.load(in);
			in.close();

			host = p.getProperty("host");
			database = p.getProperty("database");
			user = p.getProperty("user");
			password = p.getProperty("password");

			if (user == null || password == null) {
				throw new DatabaseException("Password file needs a user and a password entry.");
			}
		} catch (IOException e) {
			throw new DatabaseException("Missing password file.", e);
		}
	}

	public static ModDatabase getInstance() {
		return INSTANCE;
	}

	private String host;
	private String database;
	private String user;
	private String password;
	private Connection connection;

	private PreparedStatement insertPartStatement;

	private Multimap<String, String> inMemory = HashMultimap.<String, String> create();

	public void connect() {
		if (connection != null) {
			throw new IllegalStateException("Already connected!");
		}

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database
					+ "?useUnicode=true&characterEncoding=utf-8", user, password);

			Statement stmt = connection.createStatement();
			stmt.execute("CREATE TABLE IF NOT EXISTS PartTable (id INT PRIMARY KEY AUTO_INCREMENT, mod TEXT, part TEXT, INDEX(part(16)))");

			insertPartStatement = connection.prepareStatement("INSERT INTO PartTable (mod, part) VALUES (?, ?)");

			// Read database, create index.
			ResultSet result = stmt.executeQuery("SELECT mod, part FROM PartTable");

			while (result.next()) {
				inMemory.put(result.getString(2), result.getString(1));
			}

			result.close();
		} catch (SQLException | ClassNotFoundException e) {
			LOG.log(Level.FATAL, "Could not connect to database!", e);
		}
	}

	public void close() {
		if (connection == null) {
			throw new IllegalStateException("Not connected");
		}

		try {
			inMemory.clear();
			connection.close();
			connection = null;
		} catch (SQLException e) {
			LOG.log(Level.FATAL, "Could not close connection!", e);
		}
	}

	public void insertMod(String name, Iterable<String> parts) {
		Iterator<String> partIterator = parts.iterator();

		try {
			while (partIterator.hasNext()) {
				for (int i = 0; i < BATCH_SIZE && partIterator.hasNext(); i++) {
					String part = partIterator.next();
					
					insertPartStatement.setString(1, name);
					insertPartStatement.setString(2, part);
					insertPartStatement.addBatch();
					
					inMemory.put(part, name);
				}
				
				insertPartStatement.executeBatch();
			}
		} catch (SQLException e) {
			LOG.log(Level.FATAL, "Could insert mod!", e);
		}
	}
	
	public Collection<String> getMods(Iterable<String> parts) {
		Collection<String> mods = new HashSet<String>();
		
		for (String part :parts) {
			mods.addAll(inMemory.get(part));
		}
		
		return mods;
	}
}