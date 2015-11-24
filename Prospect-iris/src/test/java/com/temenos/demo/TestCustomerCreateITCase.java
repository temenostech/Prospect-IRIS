package com.temenos.demo;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

/**
 * Integration test for creating customers
 *
 * @author aphethean
 *
 */
public class TestCustomerCreateITCase extends FunctionalTestBase {

	@Test
	public void testSuccessfulCreate() throws Exception {
		AtomHelper atomHelper = new AtomHelper();
		String newCustomersHref = getBaseUri() + "verCustomer_Creates()/new";
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
		ClientResponse inputResponse = getAbderaClient().post(getBaseUri() + inputLink.getHref().toString(), newEntry, buildAutomXmlRequestOptions());
		assertEquals(201, inputResponse.getStatus());
		
		//Parse response
		Document<org.apache.abdera.model.Entry> responseEntry = inputResponse.getDocument();
		assertEquals("application/atom+xml", responseEntry.getContentType().toString());
		System.out.println(responseEntry.getRoot().toString());


	}

	@Test
	public void testSuccessfulCreateWithAddress() throws Exception {
		AtomHelper atomHelper = new AtomHelper();
		String newCustomersHref = getBaseUri() + "verCustomer_Creates()/new";
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
		ClientResponse inputResponse = getAbderaClient().post(getBaseUri() + inputLink.getHref().toString(), newEntry, buildAutomXmlRequestOptions());
		assertEquals(201, inputResponse.getStatus());
		
		//Parse response
		Document<org.apache.abdera.model.Entry> responseEntry = inputResponse.getDocument();
		assertEquals("application/atom+xml", responseEntry.getContentType().toString());
		System.out.println(responseEntry.getRoot().toString());
	}

	@Test
	public void testCreateWithBadAddress() throws Exception {
		AtomHelper atomHelper = new AtomHelper();
		String newCustomersHref = getBaseUri() + "verCustomer_Creates()/new";
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
		ClientResponse inputResponse = getAbderaClient().post(getBaseUri() + inputLink.getHref().toString(), newEntry, buildAutomXmlRequestOptions());
		assertEquals(400, inputResponse.getStatus());
	}

}
