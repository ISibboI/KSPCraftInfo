package de.isibboi.kspcraftinfo.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Attribute {
	private final Map<String, String> properties = new HashMap<>();
	private final String name;
	private final List<Attribute> children = new ArrayList<>();
	
	public Attribute(String name) {
		this.name = name;
	}
	
	public void setProperty(String key, String value) {
		properties.put(key, value);
	}
	
	public void addChild(Attribute attribute) {
		children.add(attribute);
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		toString(str, 0);
		return str.toString();
	}
	
	private void toString(StringBuilder str, int tabCount) {
		char[] tabChars = new char[tabCount];
		Arrays.fill(tabChars, '\t');
		String tabs = String.valueOf(tabChars);
		tabCount = tabCount + 1;
		
		str.append(tabs).append(name).append('\n');
		str.append(tabs).append("{\n");
		
		for (String key : properties.keySet()) {
			str.append(tabs).append('\t').append(key).append(" = ").append(properties.get(key)).append('\n');
		}
		
		for (Attribute child : children) {
			child.toString(str, tabCount);
		}
		
		str.append(tabs).append("}\n");
	}

	public String getName() {
		return name;
	}
}