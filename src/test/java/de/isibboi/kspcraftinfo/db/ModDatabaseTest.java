package de.isibboi.kspcraftinfo.db;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class ModDatabaseTest {
	@Test
	public void testDatabase() {
		ModDatabase db = ModDatabase.getInstance();
		db.connect();
		db.deleteAll();
		
		List<String> parts = new ArrayList<>();
		parts.add("abc");
		parts.add("def");
		parts.add("ghi");
		db.insertMod("mod", parts);
		
		parts.clear();
		parts.add("def");
		parts.add("opq");
		db.insertMod("dom", parts);
		
		Set<String> result = new HashSet<>();
		result.addAll(db.getMods(Arrays.asList("def")));
		
		Set<String> target = new HashSet<>();
		Collections.addAll(target, "mod", "dom");
		
		assertEquals("Wrong mod results", target, result);
		
		db.deleteAll();
		db.close();
	}
}