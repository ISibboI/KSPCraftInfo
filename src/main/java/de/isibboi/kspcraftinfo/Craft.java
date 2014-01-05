package de.isibboi.kspcraftinfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;

import de.isibboi.kspcraftinfo.parser.Attribute;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.graph.event.GraphEvent.Vertex;

public class Craft {
	private Part root;
private Collection<Part> partList;
private int partCount;
	
	public Craft(Attribute attribute) {
		createPartGraph(attribute);
	}

	private void createPartGraph(Attribute attribute) {
		long start = System.nanoTime();
		
		Map<String, Part> parts = new HashMap<>();
		
		for (Attribute child : attribute.getChildren()) {
			if (child.getName().equals("PART")) {
				parts.put(child.getValue("part"), new Part(child));
			}
		}

		partCount = parts.size();
		Set<String> rootCandidates = new HashSet<>();
		rootCandidates.addAll(parts.keySet());
		
		for (String part : parts.keySet()) {
			for (String succ : parts.get(part).getSuccessors()) {
				rootCandidates.remove(succ);
			}
		}
		
		if (rootCandidates.size() != 1) {
			throw new IllegalArgumentException("ksp file must contain exactly one root. (" + rootCandidates.size() + ")");
		}
		
		root = parts.get(rootCandidates.iterator().next());
		
		resolveParts(root, parts);
		
		LogManager.getLogger(getClass()).info("createPartGraph: " + (System.nanoTime() - start) + " ns");
	}

	private void resolveParts(Part current, Map<String, Part> partMap) {
		current.resolveParts(partMap);
		
		for (Part p : current.getSuccessorParts()) {
			resolveParts(p, partMap);
		}
	}
	
	public Collection<Part> getPartList() {
		if (partList == null) {
			buildPartList();
		}
		
		return partList;
	}

	private void buildPartList() {
		partList = new ArrayList<Part>(partCount);
		buildPartList(root);
	}

	private void buildPartList(Part current) {
		partList.add(current);
		
		for (Part p : current.getSuccessorParts()) {
			buildPartList(p);
		}
	}
}