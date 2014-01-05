package de.isibboi.kspcraftinfo.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public abstract class Parser<T> {
	protected final Logger log;
	
	protected Parser(Logger log) {
		this.log = log;
	}
	
	public T parse(BufferedReader in) {
		try {
			return parseInternal(in);
		} catch (IOException e) {
			log.log(Level.ERROR, "Could not parse!", e);
			return null;
		}
	}
	
	protected abstract T parseInternal(BufferedReader in) throws IOException;

	public T parse(InputStream in) {
		return parse(new BufferedReader(new InputStreamReader(in)));
	}
	
	public T parse(String in) {
		return parse(new BufferedReader(new StringReader(in)));
	}
	
	public T parse(Path in) {
		try {
			return parse(Files.newBufferedReader(in, null));
		} catch (IOException e) {
			log.log(Level.ERROR, "Could not read file!", e);
			return null;
		}
	}
	
	protected void error(String error, int lineNumber) {
		log.error("In line " + lineNumber + ": " + error);
	}
	
	protected void trace(String error, int lineNumber) {
		log.trace("In line " + lineNumber + ": " + error);
	}
}