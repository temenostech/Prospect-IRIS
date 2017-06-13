package com.temenos.marketplace;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URLEncoder;

import org.junit.Test;

import com.temenos.useragent.generic.DefaultInteractionSession;
import com.temenos.useragent.generic.InteractionSession;
import com.temenos.useragent.generic.mediatype.AtomPayloadHandler;

/**
 * TODO: Document me!
 *
 * @author jfiricel
 *
 */
public class TestCustomerITCase {

    // This test case will fail because the request entity is in a format not supported by the requested resource (Content-Type is not set)
    @Test
    public void testCreateCustomerFormatNotSupported() {
        InteractionSession sessionF1 = DefaultInteractionSession.newSession();
        sessionF1.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verCustomers()/new").post();
        assertEquals(415, sessionF1.result().code());
    }

    // This test case will be successful and it will create a temporary customer
    @Test
    public void testCreateNewCustomer() {
        InteractionSession sessionS1 = DefaultInteractionSession.newSession();
        sessionS1.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verCustomers()/new").post();
        assertEquals(201, sessionS1.result().code());
        //retrieve the customer code created
        String id = sessionS1.entities().item().get("CustomerCode");
        System.out.println("testCreateNewCustomer_id: " + id);
        System.out.println("");
    }

    // This test case will create a temporary customer and it will try to commit it to the database but without success because of missing mandatory inputs
    @Test
    public void testCreateNewCustomerMissingMandatoryInput() {
        //create a temporary customer
        InteractionSession sessionM = DefaultInteractionSession.newSession();
        sessionM.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verCustomers()/new").post();
        assertEquals(201, sessionM.result().code());
        
        //retrieve the customer code created above
        String id = sessionM.entities().item().get("CustomerCode");
        System.out.println("testCreateNewCustomerMissingMandatoryInput_id: " + id);
        
        //fill the mandatory input fields in order to save the customer into the database
        sessionM.reuse()
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/input").url()
                .post();
        assertEquals(400, sessionM.result().code());
        
        String resultErrorM = sessionM.reuse().links()
        .byRel("http://temenostech.temenos.com/rels/errors")
        .embedded().entity().get("Errors_ErrorsMvGroup(0)/Code");
        System.out.println("testCreateNewCustomerMissingMandatoryInput_error: " + resultErrorM);
        String stringToMatch = "((.*)INPUT MISSING(.*))";
        assertTrue(resultErrorM.matches(stringToMatch));
        System.out.println("");
    }

    // This test case will create a temporary customer and it will update it with the mandatory fields and commit it to the database
    @Test
    public void testCreateNewCustomerAndUpdate() throws IOException {
        //create a temporary customer
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verCustomers()/new").post();
        assertEquals(201, session.result().code());
        
        //retrieve the customer code created above
        String id = session.entities().item().get("CustomerCode");
        System.out.println("testCreateNewCustomerAndUpdate_id: " + id);
        
        //fill the mandatory input fields in order to save the customer into the database
        session.reuse()
                .set("Mnemonic", "C" + id)
                .set("verCustomer_Name1MvGroup(0)/Name1", "Mr Robin Peterson" + id)
                .set("verCustomer_ShortNameMvGroup(0)/ShortName", "Rob" + id)
                .set("Language", "1")
                .set("Sector", "1001").set("Gender", "MALE").set("Title", "MR")
                .set("FamilyName", "Peterson" + id).entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/input").url()
                .post();
        assertEquals(201, session.result().code());
        
        //check if the record created is indeed present into the database
        InteractionSession sessionCheck = DefaultInteractionSession.newSession();
        String intermediatePath = "$filter=CustomerCode eq '" + id + "'";
        String checkPath = "verCustomers()?" + URLEncoder.encode(intermediatePath, "UTF-8");
        System.out.println("testCreateNewCustomerAndUpdate_checkPath: " + checkPath);
        sessionCheck.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                    .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(checkPath).get();
        assertEquals(200, sessionCheck.result().code());
        System.out.println("");
    }

    // This test case is supposed to create a new customer into the database and then to delete it (but this delete option is not available)
    @Test
    public void testReverseNewCustomer() {
        //create a temporary customer
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verCustomers()/new").post();
        assertEquals(201, session.result().code());
        
        //retrieve the customer code created above
        String id = session.entities().item().get("CustomerCode");
        System.out.println("testReverseNewCustomer_id: " + id);
        
        //fill the mandatory input fields in order to save the customer into the database
        session.reuse()
                .set("Mnemonic", "C" + id)
                .set("verCustomer_Name1MvGroup(0)/Name1", "Mr Robin Peterson" + id)
                .set("verCustomer_ShortNameMvGroup(0)/ShortName", "Rob" + id)
                .set("Language", "1")
                .set("Sector", "1001").set("Gender", "MALE").set("Title", "MR")
                .set("FamilyName", "Peterson" + id).entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/input").url()
                .post();
        assertEquals(201, session.result().code());

        //check if the record created is indeed present into the database/retrieve the Etag value
        InteractionSession sessionCheck = DefaultInteractionSession.newSession();
        String checkPath = "verCustomers(" + id + ")";
        System.out.println("testReverseNewCustomer_checkPath: " + checkPath);
        sessionCheck.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                    .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(checkPath).get();
        String sessionCheckEtag = sessionCheck.header("ETag");
        System.out.println("testReverseNewCustomer_Etag: " + sessionCheckEtag);
        assertEquals(200, sessionCheck.result().code());

        //the record cannot be deleted from the database as of now, but the test is done to ensure this thing
        //if deletion will be available in future this test case should be updated
        InteractionSession sessionDel = DefaultInteractionSession.newSession();
        String delPath = "verCustomers(" + id + ")/reverse";
        System.out.println("testReverseNewCustomer_delPath: " + delPath);
        sessionDel.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                    .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                    .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                    .header(Configuration.HTTP_HEADER_IF_MATCH, sessionCheckEtag)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(delPath)
                    .put();
        assertEquals(400, sessionDel.result().code());

        String resultErrorDel = sessionDel.reuse().links()
                                            .byRel("http://temenostech.temenos.com/rels/errors")
                                            .embedded().entity().get("Errors_ErrorsMvGroup(0)/Code");
        System.out.println("testReverseNewCustomer_error: " + resultErrorDel);
        String stringToMatch = "((.*)NO FUNCTION FOR THIS APPLICATION(.*))";
        assertTrue(resultErrorDel.matches(stringToMatch));
        System.out.println("");
    }
    
    // This test case is supposed to create a new customer into the database and then to modify it details using 2 different sessions
    // the modification requested from second session will fail because the first session already changed the customer...
    //so a new change can be done only after a new retrieval of customer details.
    @Test
    public void testConflictWithConcurrentModificationOfACustomer() {
        //create a temporary customer
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verCustomers()/new").post();
        assertEquals(201, session.result().code());
        
        //retrieve the customer code created above
        String id = session.entities().item().get("CustomerCode");
        System.out.println("testConflictWithConcurrentModificationOfACustomer_id: " + id);
        
        //fill the mandatory input fields in order to save the customer into the database
        session.reuse()
                .set("Mnemonic", "C" + id)
                .set("verCustomer_Name1MvGroup(0)/Name1", "Mr Robin Peterson" + id)
                .set("verCustomer_ShortNameMvGroup(0)/ShortName", "Rob" + id)
                .set("Language", "1")
                .set("Sector", "1001").set("Gender", "MALE").set("Title", "MR")
                .set("FamilyName", "Peterson" + id).entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/input").url()
                .post();
        assertEquals(201, session.result().code());

        //open a first session for the created customer and retrieve the Etag value
        InteractionSession session1 = DefaultInteractionSession.newSession();
        String session1Path = "verCustomers(" + id + ")";
        System.out.println("testConflictWithConcurrentModificationOfACustomer_session1Path: " + session1Path);
        session1.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(session1Path).get();
        String session1Etag = session1.header("ETag");
        System.out.println("testConflictWithConcurrentModificationOfACustomer_Etag1: " + session1Etag);
        assertEquals(200, session1.result().code());

        //open a second session for the created customer and retrieve the Etag value
        InteractionSession session2 = DefaultInteractionSession.newSession();
        String session2Path = "verCustomers(" + id + ")";
        System.out.println("testConflictWithConcurrentModificationOfACustomer_session1Path: " + session2Path);
        session2.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(session2Path).get();
        String session2Etag = session2.header("ETag");
        System.out.println("testConflictWithConcurrentModificationOfACustomer_Etag2: " + session2Etag);
        assertEquals(200, session2.result().code());

        //change the customer information using session1
        session1.reuse()
                .header(Configuration.HTTP_HEADER_IF_MATCH, session1Etag)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .set("Gender", "FEMALE").set("Title", "MRS")
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/input").url()
                .post();
        assertEquals(201, session1.result().code());
        
        //change the customer information using session2
        session2.reuse()
                .header(Configuration.HTTP_HEADER_IF_MATCH, session2Etag)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .set("FamilyName", "Peter" + id)
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/input").url()
                .post();
        assertEquals(412, session2.result().code());
        
        String resultErrorConflict = session2.reuse().links()
                                              .byRel("http://temenostech.temenos.com/rels/errors")
                                              .embedded().entity().get("Errors_ErrorsMvGroup(0)/Code");
        System.out.println("testConflictWithConcurrentModificationOfACustomer_error: " + resultErrorConflict);
        String stringToMatch = "((.*)EB-RESOURCE.MODIFIED(.*))";
        assertTrue(resultErrorConflict.matches(stringToMatch));
        System.out.println("");
    }
}
