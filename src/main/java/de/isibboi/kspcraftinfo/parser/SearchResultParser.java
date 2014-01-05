package de.isibboi.kspcraftinfo.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SearchResultParser extends Parser<List<URI>> {
	public SearchResultParser() {
		super(LogManager.getLogger(SearchResultParser.class));
	}

	@Override
	protected List<URI> parseInternal(BufferedReader in) throws IOException {
		try {
			SAXSearchResultParser parser = new SAXSearchResultParser();
			parser.parse(new InputSource(in));
			List<String> resultStrings = parser.getResults();
			List<URI> results = new ArrayList<>(resultStrings.size());
			
			for (String result : resultStrings) {
				try {
					results.add(new URI(result));
				} catch (URISyntaxException e) {
					log.warn("Could not parse uri: " + result, e);
				}
			}
			
			return results;
		} catch (SAXException e) {
			log.log(Level.ERROR, "Could not parse search results!", e);
			return null;
		}
	}
}