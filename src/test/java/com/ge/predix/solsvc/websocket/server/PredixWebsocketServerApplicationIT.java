package com.ge.predix.solsvc.websocket.server;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.ge.predix.solsvc.restclient.impl.RestClient;
import com.ge.predix.solsvc.websocket.client.WebSocketClient;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;

/**
 * 
 * @author Predix.Adoption@ge.com -
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PredixWebsocketServerApplication.class)
@WebAppConfiguration
@IntegrationTest
public class PredixWebsocketServerApplicationIT {

	/**
	 * 
	 */
	// not declared as private--emulated by a synthetic accessor method warning
	static Logger log = LoggerFactory.getLogger(PredixWebsocketServerApplicationIT.class);

	/**
	 * 
	 */
	@Autowired
	TestWebSocketConfig testWebSocketConfig;
	
	@Autowired
	private RestClient restClient;

	@Autowired
	private WebSocketClient client;

	@Autowired
	private WebSocketClient clientError;

	/**
	 * 
	 */
	private WebSocketAdapter messageListener = new WebSocketAdapter() {
		@SuppressWarnings("nls")
		@Override
		public void onTextMessage(WebSocket wsocket, String message) {
			log.info("Recieved success message from " + wsocket.getURI() + " : " + message);
			Assert.assertTrue(message.contains("\"statusCode\": 202\"")); //$NON-NLS-1$
		}
	};

	/**
	 * 
	 */
	private WebSocketAdapter messageListenerError = new WebSocketAdapter() {
		@SuppressWarnings("nls")
		@Override
		public void onTextMessage(WebSocket wsocket, String message) {
			log.info("Recieved error message from " + wsocket.getURI() + " : " + message);
			Assert.assertTrue(message.contains("\"statusCode\": 500\"")); //$NON-NLS-1$
		}
	};

	/**
	 * 
	 */
	@Before
	public void setup() {
		List<Header> emptyHeaders = new ArrayList<Header>();
		log.debug("URI : " + this.testWebSocketConfig.getWsUri()); //$NON-NLS-1$
		this.client.init(this.restClient, emptyHeaders, this.messageListener);
		this.clientError.init(this.restClient, emptyHeaders, this.messageListenerError);
	}

	/**
	 * 
	 */
	@Test
	public void postDataTest() {

		try {
			String testMessage1 = "{\"messageId\": \"1453338376222\",\"body\": [{\"name\": \"Compressor-2015:CompressionRatio\",\"datapoints\": [[1453338376222,10,3],[1453338376222,10,1]],\"attributes\": {\"host\": \"server1\",\"customer\": \"Acme1\"}}]}"; // $$ //$NON-NLS-1$
			this.client.postTextWSData(testMessage1);

			String testMessage2 = "{\"messageId\": \"1453338376223\",\"body\": [{\"name\": \"Compressor-2016:CompressionRatio\",\"datapoints\": [[1453338376223,10,3],[1453338376223,10,1]],\"attributes\": {\"host\": \"server2\",\"customer\": \"Acme2\"}}]}"; // $$ //$NON-NLS-1$
			this.client.postTextWSData(testMessage2);

			// post data as text List
			this.client.postTextArrayWSData(generateTextArray());
		} catch (IOException e) {
			fail("Failed to connect to WS due to IOException." + e.getMessage()); // $$ //$NON-NLS-1$
		} catch (WebSocketException e) {
			fail("Failed to connect to WS due to WebSocketException." + e.getMessage()); // $$ //$NON-NLS-1$
		}

		try {// wait added for time delay in callback from websocket endpoint
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			fail("Failed due to thread interruption." + e.getMessage()); // $$ //$NON-NLS-1$
		}
	}

	/**
	 * 
	 */
	@Test
	public void postDataTestError() {

		try {

			String testMessage1 = "{\"messageId1\": \"1453338376222\",\"body\": [{\"name\": \"Compressor-2015:CompressionRatio\",\"datapoints\": [[1453338376222,10,3],[1453338376222,10,1]],\"attributes\": {\"host\": \"server1\",\"customer\": \"Acme1\"}}]}"; // $$ //$NON-NLS-1$
			this.clientError.postTextWSData(testMessage1);

			String testMessage2 = "{\"messageId1\": \"1453338376223\",\"body\": [{\"name\": \"Compressor-2016:CompressionRatio\",\"datapoints\": [[1453338376223,10,3],[1453338376223,10,1]],\"attributes\": {\"host\": \"server2\",\"customer\": \"Acme2\"}}]}"; // $$ //$NON-NLS-1$
			this.clientError.postTextWSData(testMessage2);

			// post data as text List
			this.client.postTextArrayWSData(generateTextArray());
		} catch (IOException e) {
			fail("Failed to connect to WS due to IOException." + e.getMessage()); // $$ //$NON-NLS-1$
		} catch (WebSocketException e) {
			fail("Failed to connect to WS due to WebSocketException." + e.getMessage()); // $$ //$NON-NLS-1$
		}

		try {// wait added for time delay in callback from websocket endpoint
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			fail("Failed due to thread interruption." + e.getMessage()); // $$ //$NON-NLS-1$
		}
	}

	private List<String> generateTextArray() {
		String testMessage1 = "{\"messageId\": \"1453338376210\",\"body\": [{\"name\": \"Compressor-2010:CompressionRatio\",\"datapoints\": [[1453338376223,10,3],[1453338376223,10,1]],\"attributes\": {\"host\": \"server2\",\"customer\": \"Acme2\"}}]}"; // $$ //$NON-NLS-1$
		String testMessage2 = "{\"messageId\": \"1453338376211\",\"body\": [{\"name\": \"Compressor-2011:CompressionRatio\",\"datapoints\": [[1453338376223,10,3],[1453338376223,10,1]],\"attributes\": {\"host\": \"server2\",\"customer\": \"Acme2\"}}]}"; // $$ //$NON-NLS-1$
		String testMessage3 = "{\"messageId\": \"1453338376212\",\"body\": [{\"name\": \"Compressor-2012:CompressionRatio\",\"datapoints\": [[1453338376223,10,3],[1453338376223,10,1]],\"attributes\": {\"host\": \"server2\",\"customer\": \"Acme2\"}}]}"; // $$ //$NON-NLS-1$
		String testMessage4 = "{\"messageId\": \"1453338376213\",\"body\": [{\"name\": \"Compressor-2013:CompressionRatio\",\"datapoints\": [[1453338376223,10,3],[1453338376223,10,1]],\"attributes\": {\"host\": \"server2\",\"customer\": \"Acme2\"}}]}"; // $$ //$NON-NLS-1$
		String testMessage5 = "{\"messageId\": \"1453338376214\",\"body\": [{\"name\": \"Compressor-2014:CompressionRatio\",\"datapoints\": [[1453338376223,10,3],[1453338376223,10,1]],\"attributes\": {\"host\": \"server2\",\"customer\": \"Acme2\"}}]}"; // $$ //$NON-NLS-1$

		List<String> textList = new ArrayList<String>();
		textList.add(testMessage1);
		textList.add(testMessage2);
		textList.add(testMessage3);
		textList.add(testMessage4);
		textList.add(testMessage5);
		return textList;
	}
}
