package com.temenos.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.client.AbderaClient;
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
import org.junit.Before;

/**
 * Helper operations and settings
 *
 * @author aphethean
 *
 */
public abstract class FunctionalTestBase {

	private final static String USERNAME = "SSOUSER1";
	private final static String PASSWORD = "123456";
	protected final static String BASE_URI = "http://localhost:9081/Prospect-iris/Prospect.svc/GB0010001/";
	private AbderaClient abderaClient;
	private HttpClient httpClient;
	
	@Before
	public void setup() {
		httpClient = getHttpClient();
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

	protected RequestOptions buildAutomXmlRequestOptions() {
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
