package de.isibboi.kspcraftinfo.parser;

import java.util.ArrayList;
import java.util.List;

import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.Attributes;

public class SAXSearchResultParser extends Parser {
	private boolean nextElementIsResult;
	private List<String> results = new ArrayList<String>();
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (nextElementIsResult) {
			nextElementIsResult = false;
			
			if (localName.equals("a")) {
				results.add(attributes.getValue("href"));
			}
		}
		
		if (localName.equals("div") && "search_item pod".equals(attributes.getValue("class"))) {
			nextElementIsResult = true;
		}
	}
	
	public List<String> getResults() {
		return results;
	}
}