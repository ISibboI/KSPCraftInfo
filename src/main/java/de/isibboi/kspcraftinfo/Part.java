package de.isibboi.kspcraftinfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.isibboi.kspcraftinfo.parser.Attribute;

public class Part {
	private final String name;
	private final int id;
	private final List<String> successors = new ArrayList<>();
	private final List<Part> successorParts = new ArrayList<>();
	private final boolean isDecoupler;

	public Part(String name, int id, Collection<String> successors) {
		this.name = name;
		this.id = id;
		isDecoupler = isDecoupler(name);

		if (successors != null) {
			successors.addAll(successors);
		}
	}

	public Part(String name, int id) {
		this(name, id, null);
	}

	public Part(Attribute part) {
		String combined = part.getValue("part");
		int splitIndex = combined.indexOf('_');

		if (splitIndex == -1) {
			throw new IllegalArgumentException("No _ found.");
		}

		name = combined.substring(0, splitIndex);

		try {
			id = (int) Long.parseLong(combined.substring(splitIndex + 1));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Id is not an integer: " + combined.substring(splitIndex + 1), e);
		}
		
		successors.addAll(part.getValues("link"));
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
	
	public String getTextId() {
		return name + "_" + (((long) id) & 0xFFFFFFFF);
	}

	public boolean isDecoupler() {
		return isDecoupler;
	}
	
	public List<String> getSuccessors() {
		return successors;
	}
	
	public List<Part> getSuccessorParts() {
		return successorParts;
	}
	
	public void resolveParts(Map<String, Part> partMap) {
		for (String succ : successors) {
			Part part = partMap.get(succ);
			
			if (part == null) {
				throw new IllegalArgumentException("Missing part: " + succ);
			} else {
				successorParts.add(part);
			}
		}
	}
}