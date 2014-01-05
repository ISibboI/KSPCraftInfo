package de.isibboi.kspcraftinfo.parser;

import org.junit.Test;

public class ParserTest {
	@Test
	public void testCraftParser() {
		CraftParser parser = new CraftParser();
		parser.parse(getClass().getResourceAsStream("uploads_2014_01_Interplanetary-3.craft"));
//		System.out.println(parser.getLastParseResult());
	}
}