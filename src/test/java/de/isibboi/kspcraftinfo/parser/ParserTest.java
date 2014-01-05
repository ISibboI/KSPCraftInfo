package de.isibboi.kspcraftinfo.parser;

import org.junit.Test;

public class ParserTest {
	@Test
	public void testCraftParser() {
		KspParser parser = new KspParser();
		parser.parse(getClass().getResourceAsStream("uploads_2014_01_Interplanetary-3.craft"));
	}
}