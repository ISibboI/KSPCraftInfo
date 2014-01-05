package de.isibboi.kspcraftinfo;

import java.util.HashMap;
import java.util.Map;

import de.isibboi.kspcraftinfo.parser.Attribute;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.graph.event.GraphEvent.Vertex;

public class Craft {
	private Tree<Part, Object> partGraph = new DelegateTree<>();
	
	public Craft(Attribute attribute) {
		System.out.println(attribute);
		
		createPartGraph(attribute);
	}

	private void createPartGraph(Attribute attribute) {
		Map<String, Part> parts = new HashMap<>();
		
		for (Attribute child : attribute.getChildren()) {
			if (child.getName().equals("PART")) {
				parts.put(child.getValue("part"), new Part(child));
			}
		}
		
		// TODO find root
		// TODO build graph using root and part map. 'link' means directed edge in tree.
	}
}