package de.isibboi.kspcraftinfo.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.MultiMap;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class Attribute {
	private final Multimap<String, String> properties = HashMultimap.<String, String>create();
	private final String name;
	private final List<Attribute> children = new ArrayList<>();

	public Attribute(String name) {
		this.name = name;
	}

	public void addProperty(String key, String value) {
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
	
	public List<Attribute> getChildren() {
		return children;
	}
	
	public Set<String> getProperties() {
		return properties.keySet();
	}
	
	public Collection<String> getValues(String key) {
		return properties.get(key);
	}

	public String getValue(String key) {
		return properties.get(key).iterator().next();
	}
}