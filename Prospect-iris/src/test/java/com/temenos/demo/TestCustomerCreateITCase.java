package com.temenos.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.auth.BasicScheme;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for creating customers
 *
 * @author aphethean
 *
 */
public class TestCustomerCreateITCase {

	private final static String USERNAME = "SSOUSER1";
	private final static String PASSWORD = "123456";
	private final static String BASE_URI = "http://localhost:9081/Prospect-iris/Prospect.svc/GB0010001/";
	private AbderaClient abderaClient;
	private HttpClient httpClient;
	
	@Before
	public void setup() {
		httpClient = getHttpClient();
	}
	
	@Test
	public void testSuccessfulCreate() throws Exception {
		AtomHelper atomHelper = new AtomHelper();
		String newCustomersHref = BASE_URI + "verCustomer_Creates()/new";
//		Entry blankEntry = new Abdera().newEntry();
//		ClientResponse newResponse = getAbderaClient().post(newCustomersHref, blankEntry, buildAutomXmlRequestOptions());
//		Document<Entry> responseDoc = newResponse.getDocument();
//		Entry newEntry = responseDoc.getRoot();
		
		
		Entry newEntry = post(newCustomersHref, null);
		Element customerCode = atomHelper.getEntryElement(newEntry, "CustomerCode");
		System.out.println(customerCode);

		// Populate customer details
		String prefix = RandomStringUtils.randomAlphabetic(6).toUpperCase();
		Map<String, String> testValues = new HashMap<String, String>();
		testValues.put("CustomerCode", customerCode.getText());
		testValues.put("Sector", "1001");
		testValues.put("Mnemonic", prefix);
		testValues.put("verCustomer_Create_ShortNameMvGroup.element.ShortName", prefix+"Ron");
		testValues.put("verCustomer_Create_Name1MvGroup.element.Name1", prefix+"Aaron Phethean");
		atomHelper.populateEntryProperties(newEntry, testValues);
		
		//Validate the entity
		Link inputLink = newEntry.getLink("http://temenostech.temenos.com/rels/input");
		ClientResponse inputResponse = getAbderaClient().post(BASE_URI + inputLink.getHref().toString(), newEntry, buildAutomXmlRequestOptions());
		assertEquals(201, inputResponse.getStatus());
		
		//Parse response
		Document<org.apache.abdera.model.Entry> responseEntry = inputResponse.getDocument();
		assertEquals("application/atom+xml", responseEntry.getContentType().toString());
		System.out.println(responseEntry.getRoot().toString());


	}

	@Test
	public void testSuccessfulCreateWithAddress() throws Exception {
		AtomHelper atomHelper = new AtomHelper();
		String newCustomersHref = BASE_URI + "verCustomer_Creates()/new";
//		Entry blankEntry = new Abdera().newEntry();
//		ClientResponse newResponse = getAbderaClient().post(newCustomersHref, blankEntry, buildAutomXmlRequestOptions());
//		Document<Entry> responseDoc = newResponse.getDocument();
//		Entry newEntry = responseDoc.getRoot();
		
		Entry newEntry = post(newCustomersHref, null);
		Element customerCode = atomHelper.getEntryElement(newEntry, "CustomerCode");
		System.out.println(customerCode);

		// Populate customer details
		String prefix = RandomStringUtils.randomAlphabetic(6).toUpperCase();
		Map<String, String> testValues = new HashMap<String, String>();
		testValues.put("CustomerCode", customerCode.getText());
		testValues.put("Sector", "1001");
		testValues.put("Mnemonic", prefix);
		testValues.put("verCustomer_Create_ShortNameMvGroup.element.ShortName", prefix+"Ron");
		testValues.put("verCustomer_Create_Name1MvGroup.element.Name1", prefix+"Aaron Phethean");

		// address
		testValues.put("Postcode", "4180");
		testValues.put("HouseNumber", "1");
		testValues.put("Floor", "1");
		testValues.put("Door", "13");
//		testValues.put("Road", "Fægangen");

		atomHelper.populateEntryProperties(newEntry, testValues);
		
		//Validate the entity
		Link inputLink = newEntry.getLink("http://temenostech.temenos.com/rels/input");
		ClientResponse inputResponse = getAbderaClient().post(BASE_URI + inputLink.getHref().toString(), newEntry, buildAutomXmlRequestOptions());
		assertEquals(201, inputResponse.getStatus());
		
		//Parse response
		Document<org.apache.abdera.model.Entry> responseEntry = inputResponse.getDocument();
		assertEquals("application/atom+xml", responseEntry.getContentType().toString());
		System.out.println(responseEntry.getRoot().toString());
	}

	@Test
	public void testCreateWithBadAddress() throws Exception {
		AtomHelper atomHelper = new AtomHelper();
		String newCustomersHref = BASE_URI + "verCustomer_Creates()/new";
//		Entry blankEntry = new Abdera().newEntry();
//		ClientResponse newResponse = getAbderaClient().post(newCustomersHref, blankEntry, buildAutomXmlRequestOptions());
//		Document<Entry> responseDoc = newResponse.getDocument();
//		Entry newEntry = responseDoc.getRoot();
		
		Entry newEntry = post(newCustomersHref, null);
		Element customerCode = atomHelper.getEntryElement(newEntry, "CustomerCode");
		System.out.println(customerCode);

		// Populate customer details
		String prefix = RandomStringUtils.randomAlphabetic(6).toUpperCase();
		Map<String, String> testValues = new HashMap<String, String>();
		testValues.put("CustomerCode", customerCode.getText());
		testValues.put("Sector", "1001");
		testValues.put("Mnemonic", prefix);
		testValues.put("verCustomer_Create_ShortNameMvGroup.element.ShortName", prefix+"Ron");
		testValues.put("verCustomer_Create_Name1MvGroup.element.Name1", prefix+"Aaron Phethean");

		// address
		testValues.put("Postcode", "4180");
		testValues.put("HouseNumber", "1");
		testValues.put("Floor", "999");
		testValues.put("Door", "13");
		testValues.put("Road", "Fægangen");

		atomHelper.populateEntryProperties(newEntry, testValues);
		
		//Validate the entity
		Link inputLink = newEntry.getLink("http://temenostech.temenos.com/rels/input");
		ClientResponse inputResponse = getAbderaClient().post(BASE_URI + inputLink.getHref().toString(), newEntry, buildAutomXmlRequestOptions());
		assertEquals(400, inputResponse.getStatus());
	}

	
	protected Entry post(String href, NameValuePair[] params) throws Exception {

		// Obtain a new entry
		PostMethod method = new PostMethod(href);
		try {
			method.setRequestEntity(new StringRequestEntity("", "application/atom+xml", "UTF-8"));
	    	method.setDoAuthentication(true);		//Require authentication
			if (params != null) {
				method.setQueryString(params);
			}
	    	httpClient.executeMethod(method);
	    	assertEquals(HttpStatus.SC_CREATED, method.getStatusCode());

			// read as string for debugging
			String response = method.getResponseBodyAsString();
//			System.out.println("Response = " + response);

			Abdera abdera = new Abdera();
			Parser parser = abdera.getParser();
			Document<org.apache.abdera.model.Entry> doc = parser.parse(new StringReader(response));
			return doc.getRoot();
		} catch (IOException e) {
			fail(e.getMessage());
		} finally {
			method.releaseConnection();
		}
		return null;
	}

	/**
	 * Returns an Abdera client with authentication support
	 * @return
	 */
	protected AbderaClient getAbderaClient() {
		if(abderaClient == null) {
			abderaClient = createAbderaClient(USERNAME, PASSWORD);
		}
		return abderaClient;
	}
	
	protected AbderaClient createAbderaClient(String username, String password) {
		AbderaClient abClient = new AbderaClient(new Abdera());

		AbderaClient.registerTrustManager(); // needed for SSL
		AbderaClient.registerScheme(AuthPolicy.BASIC, BasicScheme.class);
		abClient.setAuthenticationSchemePriority(AuthPolicy.BASIC);
		abClient.usePreemptiveAuthentication(false);
		try {
			abClient.addCredentials(AuthScope.ANY_HOST, AuthScope.ANY_REALM, AuthScope.ANY_SCHEME, 
					new UsernamePasswordCredentials(username, password));
		} catch(URISyntaxException ue) {
			ue.printStackTrace();
			fail("Failed to set authentication scheme: " + ue.getMessage());
		}
		return abClient;
	}

	private RequestOptions buildAutomXmlRequestOptions() {
		RequestOptions requestOptions = new RequestOptions();
		requestOptions.setAccept("application/atom+xml");
		requestOptions.setContentType("application/atom+xml;type=entry");
		return requestOptions;
	}

	/**
	 * Return an HTTP client with authentication support
	 * @return
	 */
	protected HttpClient getHttpClient() {
		if(httpClient == null) {
			httpClient = new HttpClient();
			httpClient.getState().setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials(USERNAME, PASSWORD));
		}
    	return httpClient;
	}


}
