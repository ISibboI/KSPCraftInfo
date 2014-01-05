package de.isibboi.kspcraftinfo;

import de.isibboi.kspcraftinfo.parser.Attribute;

public class Part {
	private final String name;
	private final int id;
	private final boolean isDecoupler;

	public Part(String name, int id) {
		this.name = name;
		this.id = id;
		isDecoupler = isDecoupler(name);
	}

	public Part(Attribute part) {
		String combined = part.getValue("part");
		int splitIndex = combined.indexOf('_');
		
		if (splitIndex == -1) {
			throw new IllegalArgumentException("No _ found.");
		}
		
		name = combined.substring(0, splitIndex);
		
		try {
			id = Integer.parseInt(combined.substring(splitIndex + 1) );
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Id is not an integer.", e);
		}
		
		isDecoupler = isDecoupler();
	}

	private boolean isDecoupler(String name) {
		// TODO
		return false;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public boolean isDecoupler() {
		return isDecoupler;
	}
}