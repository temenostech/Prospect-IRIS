package com.temenos.demo;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;
import org.apache.abdera.protocol.client.ClientResponse;
import org.junit.Test;

/**
 * Integration test for creating customers
 *
 * @author aphethean
 *
 */
public class TestPOAITCase extends FunctionalTestBase {

	@Test
	public void testSuccessfulCreate() throws Exception {
		AtomHelper atomHelper = new AtomHelper();
		String newCustomersHref = BASE_URI + "verCrContactLog_CaptureContacts()/new";
//		Entry blankEntry = new Abdera().newEntry();
//		ClientResponse newResponse = getAbderaClient().post(newCustomersHref, blankEntry, buildAutomXmlRequestOptions());
//		Document<Entry> responseDoc = newResponse.getDocument();
//		Entry newEntry = responseDoc.getRoot();
		
		
		Entry newEntry = post(newCustomersHref, null);
		Element customerCode = atomHelper.getEntryElement(newEntry, "ClientContactLog");
		System.out.println(customerCode);

		// Populate contact details
		Map<String, String> testValues = new HashMap<String, String>();
		testValues.put("ContactClient", "128078");
		testValues.put("ContactStatus", "NEW");
		testValues.put("ContactDesc", "POA Request");
		testValues.put("ContactDate", "22");
		testValues.put("ContactTime", "10:00");
		testValues.put("ContactType", "TASK");
		testValues.put("ContactChannel", "INTERNET");
		testValues.put("verCrContactLog_CaptureContact_ContactNotesMvGroup.element.verCrContactLog_CaptureContact_ContactNotesSvGroup.element.ContactNotes", 
				"Please grant POA to my daughter");
//		testValues.put("verCrContactLog_CaptureContact_ContactNotesMvGroup.element.verCrContactLog_CaptureContact_ContactNotesSvGroup.element.ContactNotes", 
//				"Jenny Gates, born 8 August 1978");
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


}
