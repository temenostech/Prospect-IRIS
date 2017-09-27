package com.temenos.marketplace;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
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

public class TestTeller_LcyCashinITCase {

    // This test case will fail because the request entity is in a format not supported by the requested resource
    @Test
    public void testTellerLcyCashin_CreateFailure() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verTeller_LcyCashins()/new").post();
        assertEquals(415, session.result().code());
    }
    
 // This test case will fail because the user used is not having the TILL opened
    @Test
    public void testTellerLcyCashin_CreateFailureTillNotOpen() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verTeller_LcyCashins()/new").post();
        assertEquals(400, session.result().code());
        
        String resultError = session.entities().item().get("Errors_ErrorsMvGroup(0)/Code");
        System.out.println("testTellerLcyCashin_CreateFailureTillNotOpen_error: " + resultError);
        String stringToMatch = "((.*)TILL NOT OPEN(.*))";
        assertTrue(resultError.matches(stringToMatch));
        System.out.println("");
    }
   
    // This test case will be successful and it will create a temporary teller transaction
    @Test
    public void testTellerLcyCashin_Create() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                .basicAuth(Configuration.TELLER_USER_NAME,Configuration.TELLER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verTeller_LcyCashins()/new").post();
        assertEquals(201, session.result().code());
    }

    // This test case will be successful and it will create a temporary teller transaction that is committed into the database also
    @Test
    public void testTellerLcyCashin_CreateAndUpdate() throws UnsupportedEncodingException {
        // create a temporary teller cashin transaction
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                .basicAuth(Configuration.TELLER_USER_NAME,Configuration.TELLER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verTeller_LcyCashins()/new").post();
        assertEquals(201, session.result().code());
        
        //retrieve the teller cashin transaction number created above
        String id = session.entities().item().get("TransactionNumber");
        System.out.println("testTellerLcyCashin_CreateAndUpdate_id: " + id);
        
        //fill the mandatory input fields in order to commit the teller cashin transaction into the database
        session.reuse()
                .set("verTeller_LcyCashin_Account1MvGroup(0)/AmountLocal1", "200")
                .set("Account2", "83267")
                .set("verTeller_LcyCashin_DrDenomMvGroup(0)/DrUnit", "2")
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/input").url()
                .post();
        // in case of error returned then retrieve the error code and error info
        if (session.result().code() != 201) {
            String resultErrorDel = session.reuse().links()
                                            .byRel("http://temenostech.temenos.com/rels/errors")
                                            .embedded().entity().get("Errors_ErrorsMvGroup(0)/Code");
            System.out.println("testTellerLcyCashin_CreateAndUpdate_error: " + resultErrorDel);

            String resultInfoErrorDel = session.reuse().links()
                    .byRel("http://temenostech.temenos.com/rels/errors")
                    .embedded().entity().get("Errors_ErrorsMvGroup(0)/Info");
            System.out.println("testTellerLcyCashin_CreateAndUpdate_InfoError: " + resultInfoErrorDel);
        }
        assertEquals(201, session.result().code());

        //check if the teller cashin transaction just created is indeed present into the database
        InteractionSession sessionCheck = DefaultInteractionSession.newSession();
        String intermediatePath = "$filter=TransactionNumber eq '" + id + "'";
        String checkPath = "verTeller_LcyCashins()?" + URLEncoder.encode(intermediatePath, "UTF-8");
        System.out.println("testTellerLcyCashin_CreateAndUpdate_checkPath: " + checkPath);
        sessionCheck.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                    .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(checkPath).get();
        assertEquals(200, sessionCheck.result().code());
        System.out.println("");
    }

    // This test case is creating a temporary teller cashin transaction and then is validating it without success
    @Test
    public void testTellerLcyCashin_CreateAndValidateFailure() {
        // create a temporary teller cashin transaction
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                .basicAuth(Configuration.TELLER_USER_NAME,Configuration.TELLER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verTeller_LcyCashins()/new").post();
        assertEquals(201, session.result().code());
        
        //retrieve the teller cashin transaction number created above
        String id = session.entities().item().get("TransactionNumber");
        System.out.println("testTellerLcyCashin_CreateAndValidateFailure_id: " + id);
        
        //validate the teller cashin created by not filling the mandatory input fields
        session.reuse()
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/validate").url()
                .post();
        assertEquals(400, session.result().code());
        //retrieve the error code returned
        
        String resultErrorValidate = session.reuse().links()
                                            .byRel("http://temenostech.temenos.com/rels/errors")
                                            .embedded().entity().get("Errors_ErrorsMvGroup(0)/Code");
        System.out.println("testTellerLcyCashin_CreateAndValidateFailure_error: " + resultErrorValidate);
        String stringToMatch = "((.*)INPUT MISSING(.*))";
        assertTrue(resultErrorValidate.matches(stringToMatch));
        
        System.out.println("");
    }
    
    // This test case is creating a temporary teller cashin transaction and then is successfully validating it
    @Test
    public void testTellerLcyCashin_CreateAndValidateSuccess() {
        // create a temporary teller cashin transaction
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                .basicAuth(Configuration.TELLER_USER_NAME,Configuration.TELLER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verTeller_LcyCashins()/new").post();
        assertEquals(201, session.result().code());
        
        //retrieve the teller cashin transaction number created above
        String id = session.entities().item().get("TransactionNumber");
        System.out.println("testTellerLcyCashin_CreateAndValidateSuccess_id: " + id);
        
        //fill the mandatory input fields in order to commit the teller cashin transaction into the database
        session.reuse()
                .set("verTeller_LcyCashin_Account1MvGroup(0)/AmountLocal1", "100")
                .set("Account2", "83267")
                .set("verTeller_LcyCashin_DrDenomMvGroup(0)/DrUnit", "1")
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/validate").url()
                .post();
        assertEquals(200, session.result().code());
        System.out.println("");
    }
    
    // This test case is creating a temporary teller cashin transaction, it is successfully validating it and then it is committing it into the database
    @Test
    public void testTellerLcyCashin_CreateAndValidateAndUpdateSuccess() {
        // create a temporary teller cashin transaction
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                .basicAuth(Configuration.TELLER_USER_NAME,Configuration.TELLER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verTeller_LcyCashins()/new").post();
        assertEquals(201, session.result().code());
        
        //retrieve the teller cashin transaction number created above
        String id = session.entities().item().get("TransactionNumber");
        System.out.println("testTellerLcyCashin_CreateAndValidateAndUpdateSuccess_id: " + id);
        
        //fill the mandatory input fields in order to validate the teller cashin transaction
        session.reuse()
                .set("verTeller_LcyCashin_Account1MvGroup(0)/AmountLocal1", "100")
                .set("Account2", "83267")
                .set("verTeller_LcyCashin_DrDenomMvGroup(0)/DrUnit", "1")
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/validate").url()
                .post();
        assertEquals(200, session.result().code());
        
      //commit the teller cashin transaction that was validated above into the database
        session.reuse()
        .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/input").url()
                .post();
        assertEquals(201, session.result().code());
        System.out.println("");
    }
    
    // This test case is creating a temporary teller transaction and then is putting it on hold
    @Test
    public void testTellerLcyCashin_CreateAndHold() {
        // create a temporary teller cashin transaction
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                .basicAuth(Configuration.TELLER_USER_NAME,Configuration.TELLER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verTeller_LcyCashins()/new").post();
        assertEquals(201, session.result().code());
        
        //retrieve the teller cashin transaction number created above
        String id = session.entities().item().get("TransactionNumber");
        System.out.println("testTellerLcyCashin_CreateAndHold_id: " + id);
        
        //fill the mandatory input fields and put the teller cashin transaction on hold into the database
        //Note: it is not mandatory to set the fields but it seems useless to create an empty record (being set to IHLD the validations are not triggered)
        session.reuse()
                .set("verTeller_LcyCashin_Account1MvGroup(0)/AmountLocal1", "100")
                .set("Account2", "83267")
                .set("verTeller_LcyCashin_DrDenomMvGroup(0)/DrUnit", "1")
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/hold").url()
                .post();
        assertEquals(201, session.result().code());

        //check the value of the recordStatus, it must be IHLD
        InteractionSession seeSession = DefaultInteractionSession.newSession();
        String seeSessionPath = "verTeller_LcyCashins(" + id + ")/see";
        System.out.println("testTellerLcyCashin_CreateAndHold_seeSessionPath: " + seeSessionPath);
        seeSession.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(Configuration.TELLER_USER_NAME,Configuration.TELLER_PASSWORD)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(seeSessionPath).get();
        assertEquals(200, seeSession.result().code());
        String recordStatus = seeSession.entities().item().get("RecordStatus");
        assertEquals("IHLD", recordStatus);
        System.out.println("");
    }
    
    // This test case is creating a temporary teller transaction, is putting it on hold  and then it will delete it
    @Test
    public void testTellerLcyCashin_CreateAndHoldAndDelete() {
        // create a temporary teller cashin transaction
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                .basicAuth(Configuration.TELLER_USER_NAME,Configuration.TELLER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verTeller_LcyCashins()/new").post();
        assertEquals(201, session.result().code());
        
        
        //retrieve the teller cashin transaction number created above
        String id = session.entities().item().get("TransactionNumber");
        System.out.println("testTellerLcyCashin_CreateAndHoldAndDelete_id: " + id);
        
        //fill the mandatory input fields and put the teller cashin transaction on hold into the database
        //Note: it is not mandatory to set the fields but it seems useless to create an empty record (being set to IHLD the validations are not triggered)
        session.reuse()
                .set("verTeller_LcyCashin_Account1MvGroup(0)/AmountLocal1", "100")
                .set("Account2", "83267")
                .set("verTeller_LcyCashin_DrDenomMvGroup(0)/DrUnit", "1")
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/hold").url()
                .post();
        assertEquals(201, session.result().code());

        //check the value of the recordStatus, it must be IHLD
        InteractionSession seeSession = DefaultInteractionSession.newSession();
        String seeSessionPath = "verTeller_LcyCashins(" + id + ")/see";
        System.out.println("testTellerLcyCashin_CreateAndHoldAndDelete_seeSessionPath: " + seeSessionPath);
        seeSession.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(Configuration.TELLER_USER_NAME,Configuration.TELLER_PASSWORD)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(seeSessionPath).get();
        assertEquals(200, seeSession.result().code());
        String recordStatus = seeSession.entities().item().get("RecordStatus");
        System.out.println("testTellerLcyCashin_CreateAndHoldAndDelete_RecordStatus: " + recordStatus);
        assertEquals("IHLD", recordStatus);
        
        //open a new session for the teller cashin transaction that was put on hold and retrieve the Etag value
        InteractionSession delSession = DefaultInteractionSession.newSession();
        String delSessionPath = "verTeller_LcyCashins(" + id + ")";
        System.out.println("testTellerLcyCashin_CreateAndHoldAndDelete_delSessionPath: " + delSessionPath);
        delSession.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(Configuration.TELLER_USER_NAME,Configuration.TELLER_PASSWORD)
                    .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(delSessionPath)
                    .get();
        String delSessionEtag = delSession.header("ETag");
        System.out.println("testTellerLcyCashin_CreateAndHoldAndDelete_delSessionEtag: " + delSessionEtag);
        assertEquals(200, delSession.result().code());
        
        //delete customer in status IHLD
        //NOTE: The record is successfully deleted but the status code is 500 --> to be checked this issue
        delSession.reuse()
                    .header(Configuration.HTTP_HEADER_IF_MATCH, delSessionEtag)
                    .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                    .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                    .entities().item().links()
                    .byRel("http://temenostech.temenos.com/rels/delete").url()
                    .delete();
        assertEquals(500, delSession.result().code());
        
        //check if indeed the teller cashin transaction is not present anymore into the database
        InteractionSession checkDelSession = DefaultInteractionSession.newSession();
        String checkDelSessionPath = "verTeller_LcyCashins(" + id + ")/see";
        System.out.println("testTellerLcyCashin_CreateAndHoldAndDelete_checkDelSessionPath: " + checkDelSessionPath);
        checkDelSession.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(Configuration.TELLER_USER_NAME,Configuration.TELLER_PASSWORD)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(checkDelSessionPath).get();
        assertEquals(404, checkDelSession.result().code());
        String resultCode = checkDelSession.entities().item().get("Errors_ErrorsMvGroup(0)/Code");
        assertEquals("RECORD MISSING", resultCode);
        System.out.println("");
    }
    
    // This test case will create a temporary teller cashin transaction, commit it into the database and then reverse it
    @Test
    public void testTellerLcyCashin_CreateAndUpdateAndReverse() {
        // create a temporary teller cashin transaction
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                .basicAuth(Configuration.TELLER_USER_NAME,Configuration.TELLER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verTeller_LcyCashins()/new").post();
        assertEquals(201, session.result().code());
        
        //retrieve the teller cashin transaction number created above
        String id = session.entities().item().get("TransactionNumber");
        System.out.println("testTellerLcyCashin_CreateAndUpdateAndReverse_id: " + id);
        
        //fill the mandatory input fields in order to commit the teller cashin transaction into the database
        session.reuse()
                .set("verTeller_LcyCashin_Account1MvGroup(0)/AmountLocal1", "100")
                .set("Account2", "83267")
                .set("verTeller_LcyCashin_DrDenomMvGroup(0)/DrUnit", "1")
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/input").url()
                .post();
        assertEquals(201, session.result().code());

        //check if the teller cashin transaction just created is indeed present into the database and retrieve the Etag value
        InteractionSession reverseSession = DefaultInteractionSession.newSession();
        String checkPath = "verTeller_LcyCashins(" + id + ")";
        System.out.println("testTellerLcyCashin_CreateAndUpdateAndReverse_reversePath: " + checkPath);
        reverseSession.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(Configuration.TELLER_USER_NAME,Configuration.TELLER_PASSWORD)
                    .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(checkPath)
                    .get();
        String reverseSessionEtag = reverseSession.header("ETag");
        System.out.println("testTellerLcyCashin_CreateAndUpdateAndReverse_reverseSessionEtag: " + reverseSessionEtag);
        assertEquals(200, reverseSession.result().code());
        
        //reverse the teller cashin transaction
        reverseSession.reuse()
                .header(Configuration.HTTP_HEADER_IF_MATCH, reverseSessionEtag)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/reverse").url()
                .put();
        assertEquals(200, reverseSession.result().code());
        
        //check the value of the recordStatus for the teller cashin transaction that was just reversed, it must be REVE
        InteractionSession seeSession = DefaultInteractionSession.newSession();
        String seeSessionPath = "verTeller_LcyCashins(" + id + ")/see";
        System.out.println("testTellerLcyCashin_CreateAndUpdateAndReverse_seeSessionPath: " + seeSessionPath);
        seeSession.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(Configuration.TELLER_USER_NAME,Configuration.TELLER_PASSWORD)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(seeSessionPath).get();
        assertEquals(200, seeSession.result().code());
        String recordStatus = seeSession.entities().item().get("RecordStatus");
        System.out.println("testTellerLcyCashin_CreateAndUpdateAndReverse_RecordStatus: " + recordStatus);
        assertEquals("REVE", recordStatus);
        
        System.out.println("");
        
    }
    
    // This test case will retrieve all available teller cashin transactions
    @Test
    public void testTellerLcyCashin_RetrieveTxns() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verTeller_LcyCashins()").get();
        assertEquals(200, session.result().code());
    }
    
    // This test case will retrieve the details of a teller cashin transaction
    @Test
    public void testTellerLcyCashin_RetrieveDetailsSpecificTxn() {
        // retrieve all existing teller cashin transactions
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verTeller_LcyCashins()").get();
        assertEquals(200, session.result().code());
        
        //retrieve the id of the first teller cashin transaction returned
        String id = session.entities().collection().get(0).get("TransactionNumber");
        System.out.println("testTellerLcyCashin_RetrieveDetailsSpecificTxn_id: " + id);
        
        // retrieve the details of the specific teller cashin transaction
        String specificPath = "verTeller_LcyCashins(" + id + ")";
        InteractionSession session2 = DefaultInteractionSession.newSession();
        session2.basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
        .url()
        .baseuri(Configuration.DATA_SERVICE_URL)
        .path(specificPath).get();
        assertEquals(200, session.result().code());
        
        System.out.println("");
    }
}
