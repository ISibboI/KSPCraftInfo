package de.isibboi.kspcraftinfo.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

import org.apache.logging.log4j.LogManager;

import de.isibboi.kspcraftinfo.Craft;

public class KspParser extends Parser<Craft> {
	private Attribute lastParseResult;
	
	protected KspParser() {
		super(LogManager.getLogger(KspParser.class));
	}

	@Override
	protected Craft parseInternal(BufferedReader in) throws IOException {
		long start = System.nanoTime();
		
		Deque<Attribute> attributes = new ArrayDeque<>();
		attributes.push(new Attribute("ROOT"));
		String line;
		int lineNumber = 1;
		boolean hasLines = true;
		boolean openingBraceRequired = false;
		lastParseResult = null;

		while (hasLines) {
			line = in.readLine();

			if (line == null) {
				hasLines = false;
			} else {
				line = line.trim();

				if (!line.isEmpty()) {
					if (openingBraceRequired) {
						openingBraceRequired = false;

						if (line.equals("{")) {
							trace("Found opening brace.", lineNumber);
						} else {
							log.error("Opening brace required after block definition.", lineNumber);
							return null;
						}
					} else if (line.equals("}")) {
						trace("Found closing brace.", lineNumber);

						if (attributes.size() < 2) {
							error("Too much closing braces.", lineNumber);
						}

						attributes.pop();
					} else {
						int separatorIndex = line.indexOf('=');

						if (separatorIndex != -1) {
							String key = line.substring(0, separatorIndex).trim();
							String value = line.substring(separatorIndex + 1);

							if (key.isEmpty()) {
								error("No key found.", lineNumber);
								return null;
							}

							attributes.peek().addProperty(key, value);
						} else {
							Attribute newBlock = new Attribute(line);
							attributes.peek().addChild(newBlock);
							attributes.push(newBlock);
							openingBraceRequired = true;
						}
					}
				}
			}

			lineNumber++;
		}
		
		if (attributes.size() > 1) {
			error("Missing closing brace.", lineNumber);
			return null;
		} else {
			log.info("parseInternal: " + (System.nanoTime() - start) + " ns");
			
			lastParseResult = attributes.peek();
			return new Craft(attributes.pop());
		}
	}
	
	public Attribute getLastParseResult() {
		return lastParseResult;
	}
}