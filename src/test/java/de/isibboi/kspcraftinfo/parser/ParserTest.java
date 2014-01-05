package de.isibboi.kspcraftinfo.parser;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

public class ParserTest {
	@Test
	public void testKspParser() {
		KspParser parser = new KspParser();
		parser.parse(getClass().getResourceAsStream("uploads_2014_01_Interplanetary-3.craft"));
	}
	
	@Test
	public void testSearchResultParser() throws IOException {
		CloseableHttpClient client= HttpClients.createDefault();
		HttpGet request = new HttpGet("http://kerbalspaceport.com/?s=+");
		CloseableHttpResponse response = client.execute(request);
		
		SearchResultParser parser = new SearchResultParser();
		List<URI> results = parser.parse(response.getEntity().getContent());
		
		assertEquals("Wrong result number", 9, results.size());
//		System.out.println(Arrays.toString(results.toArray()));
	}
}