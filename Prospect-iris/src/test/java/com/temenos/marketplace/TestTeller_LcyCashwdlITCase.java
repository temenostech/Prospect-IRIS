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
public class TestTeller_LcyCashwdlITCase {

    public static String TELLER_USER_NAME = "CSAGENT";
    public static String TELLER_PASSWORD = "123456";

    // This test case will fail because the request entity is in a format not supported by the requested resource
    @Test
    public void testTellerLcyCashwdl_createFailure() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verTeller_LcyCashwdls()/new").post();
        assertEquals(415, session.result().code());
    }
    
 // This test case will fail because the user used is not having the TILL opened
    @Test
    public void testTellerLcyCashwdl_CreateFailureTillNotOpen() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verTeller_LcyCashwdls()/new").post();
        assertEquals(400, session.result().code());
        
        String resultError = session.entities().item().get("Errors_ErrorsMvGroup(0)/Code");
        System.out.println("testTellerLcyCashwdl_CreateFailureTillNotOpen_error: " + resultError);
        String stringToMatch = "((.*)TILL NOT OPEN(.*))";
        assertTrue(resultError.matches(stringToMatch));
        System.out.println("");
    }
   
    // This test case will be successful and it will create a temporary teller transaction
    @Test
    public void testTellerLcyCashwdl_Create() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                .basicAuth(TELLER_USER_NAME,TELLER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verTeller_LcyCashwdls()/new").post();
        assertEquals(201, session.result().code());
    }

    // This test case will be successful and it will create a temporary teller transaction that is committed into the database also
    @Test
    public void testTellerLcyCashwdl_CreateAndUpdate() throws UnsupportedEncodingException {
        // create a temporary teller cashwdl transaction
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                .basicAuth(TELLER_USER_NAME,TELLER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verTeller_LcyCashwdls()/new").post();
        assertEquals(201, session.result().code());
        
        //retrieve the teller cashwdl transaction number created above
        String id = session.entities().item().get("TransactionNumber");
        System.out.println("testTellerLcyCashwdl_CreateAndUpdate_id: " + id);
        
        //fill the mandatory input fields in order to commit the teller cashwdl transaction into the database
        session.reuse()
                .set("verTeller_LcyCashwdl_Account1MvGroup(0)/AmountLocal1", "100")
                .set("Account2", "83267")
                .set("ValueDate2", "20170417")
                .set("verTeller_LcyCashwdl_DenominationMvGroup(0)/Unit", "1")
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/input").url()
                .post();
        // in case of error returned then retrieve the error code and error info
        if (session.result().code() != 201) {
            String resultErrorDel = session.reuse().links()
                                            .byRel("http://temenostech.temenos.com/rels/errors")
                                            .embedded().entity().get("Errors_ErrorsMvGroup(0)/Code");
            System.out.println("testTellerLcyCashwdl_CreateAndUpdate_error: " + resultErrorDel);

            String resultInfoErrorDel = session.reuse().links()
                                                        .byRel("http://temenostech.temenos.com/rels/errors")
                                                        .embedded().entity().get("Errors_ErrorsMvGroup(0)/Info");
            System.out.println("testTellerLcyCashwdl_CreateAndUpdate_InfoError: " + resultInfoErrorDel);           
        }
        assertEquals(201, session.result().code());

        //retrieve the StatusRecord of the just created teller cashwdl transaction
        String recordStatusCreate = session.entities().item().get("RecordStatus");
        System.out.println("testTellerLcyCashwdl_CreateAndUpdate_RecordStatusCreate: " + recordStatusCreate);
        //retrieve the session ETag in order to be used down to the authorization step
        String sessionEtag = session.header("ETag");
        System.out.println("testTellerLcyCashwdl_CreateAndUpdate_sessionEtag: " + sessionEtag);
        
        //Authorize the creation of the teller cashwdl transaction into the database
        session.reuse()
                .header(Configuration.HTTP_HEADER_IF_MATCH, sessionEtag)
                .basicAuth(Configuration.AUTHORISER_USER_NAME, Configuration.AUTHORISER_PASSWORD).links()
                .byRel("http://temenostech.temenos.com/rels/authorise").url()
                .put();
        assertEquals(200, session.result().code());

        //check if the teller cashwdl transaction just created is indeed present into the database
        InteractionSession sessionCheck = DefaultInteractionSession.newSession();
        String intermediatePath = "$filter=TransactionNumber eq '" + id + "'";
        String checkPath = "verTeller_LcyCashwdls()?" + URLEncoder.encode(intermediatePath, "UTF-8");
        System.out.println("testTellerLcyCashwdl_CreateAndUpdate_checkPath: " + checkPath);
        sessionCheck.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                    .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(checkPath).get();
        assertEquals(200, sessionCheck.result().code());
        System.out.println("");
    }
    
    // This test case is creating a temporary teller cashwdl transaction and then is validating it without success
    @Test
    public void testTellerLcyCashwdl_CreateAndValidateFailure() {
        // create a temporary teller cashwdl transaction
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                .basicAuth(TELLER_USER_NAME,TELLER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verTeller_LcyCashwdls()/new").post();
        assertEquals(201, session.result().code());
        
        //retrieve the teller cashwdl transaction number created above
        String id = session.entities().item().get("TransactionNumber");
        System.out.println("testTellerLcyCashwdl_CreateAndValidateFailure_id: " + id);
        
        //validate the teller cachin created by not filling the mandatory input fields
        session.reuse()
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/validate").url()
                .post();
        assertEquals(400, session.result().code());
        //retrieve the error code returned
        
        String resultErrorValidate = session.reuse().links()
                                            .byRel("http://temenostech.temenos.com/rels/errors")
                                            .embedded().entity().get("Errors_ErrorsMvGroup(0)/Code");
        System.out.println("testTellerLcyCashwdl_CreateAndValidateFailure_error: " + resultErrorValidate);
        String stringToMatch = "((.*)INPUT MISSING(.*))";
        assertTrue(resultErrorValidate.matches(stringToMatch));
        
        System.out.println("");
    }
    
    // This test case is creating a temporary teller cashwdl transaction and then is successfully validating it
    @Test
    public void testTellerLcyCashwdl_CreateAndValidateSuccess() {
        // create a temporary teller cashwdl transaction
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                .basicAuth(TELLER_USER_NAME,TELLER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verTeller_LcyCashwdls()/new").post();
        assertEquals(201, session.result().code());
        
        //retrieve the teller cashwdl transaction number created above
        String id = session.entities().item().get("TransactionNumber");
        System.out.println("testTellerLcyCashwdl_CreateAndValidateSuccess_id: " + id);
        
        //fill the mandatory input fields in order to commit the teller cashwdl transaction into the database
        session.reuse()
                .set("verTeller_LcyCashwdl_Account1MvGroup(0)/AmountLocal1", "100")
                .set("Account2", "83267")
                .set("ValueDate2", "20170417")
                .set("verTeller_LcyCashwdl_DenominationMvGroup(0)/Unit", "1")
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/validate").url()
                .post();
        assertEquals(200, session.result().code());
        System.out.println("");
    }
    
    // This test case is creating a temporary teller cashwdl transaction, it is successfully validating it and then it is committing it into the database
    @Test
    public void testTellerLcyCashwdl_CreateAndValidateAndUpdateSuccess() {
        // create a temporary teller cashwdl transaction
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                .basicAuth(TELLER_USER_NAME,TELLER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verTeller_LcyCashwdls()/new").post();
        assertEquals(201, session.result().code());
        
        //retrieve the teller cashwdl transaction number created above
        String id = session.entities().item().get("TransactionNumber");
        System.out.println("testTellerLcyCashwdl_CreateAndValidateAndUpdateSuccess_id: " + id);
        
        //fill the mandatory input fields in order to validate the teller cashwdl transaction
        session.reuse()
                .set("verTeller_LcyCashwdl_Account1MvGroup(0)/AmountLocal1", "100")
                .set("Account2", "83267")
                .set("ValueDate2", "20170417")
                .set("verTeller_LcyCashwdl_DenominationMvGroup(0)/Unit", "1")
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/validate").url()
                .post();
        assertEquals(200, session.result().code());
        
      //commit the teller cashwdl transaction that was validated above into the database
        session.reuse()
        .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/input").url()
                .post();
        assertEquals(201, session.result().code());
        
        //retrieve the StatusRecord of the just created teller cashwdl transaction
        String recordStatusCreate = session.entities().item().get("RecordStatus");
        System.out.println("testTellerLcyCashwdl_CreateAndValidateAndUpdateSuccess_RecordStatusCreate: " + recordStatusCreate);
        //retrieve the session ETag in order to be used down to the authorization step
        String sessionEtag = session.header("ETag");
        System.out.println("testTellerLcyCashwdl_CreateAndValidateAndUpdateSuccess_sessionEtag: " + sessionEtag);
        
        //Authorize the creation of the teller cashwdl transaction into the database
        session.reuse()
                .header(Configuration.HTTP_HEADER_IF_MATCH, sessionEtag)
                .basicAuth(Configuration.AUTHORISER_USER_NAME, Configuration.AUTHORISER_PASSWORD).links()
                .byRel("http://temenostech.temenos.com/rels/authorise").url()
                .put();
        assertEquals(200, session.result().code());
        
        System.out.println("");
    }
    
    // This test case is creating a temporary teller transaction and then is putting it on hold
    @Test
    public void testTellerLcyCashwdl_CreateAndHold() {
        // create a temporary teller cashwdl transaction
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                .basicAuth(TELLER_USER_NAME,TELLER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verTeller_LcyCashwdls()/new").post();
        assertEquals(201, session.result().code());
        
        //retrieve the teller cashwdl transaction number created above
        String id = session.entities().item().get("TransactionNumber");
        System.out.println("testTellerLcyCashwdl_CreateAndHold_id: " + id);
        
        //fill the mandatory input fields and put the teller cachin transaction on hold into the database
        //Note: it is not mandatory to set the fields but it seems useless to create an empty record (being set to IHLD the validations are not triggered)
        session.reuse()
                .set("verTeller_LcyCashwdl_Account1MvGroup(0)/AmountLocal1", "100")
                .set("Account2", "83267")
                .set("ValueDate2", "20170417")
                .set("verTeller_LcyCashwdl_DenominationMvGroup(0)/Unit", "1")
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/hold").url()
                .post();
        assertEquals(201, session.result().code());

        //check the value of the recordStatus, it must be IHLD
        InteractionSession seeSession = DefaultInteractionSession.newSession();
        String seeSessionPath = "verTeller_LcyCashwdls(" + id + ")/see";
        System.out.println("testTellerLcyCashwdl_CreateAndHold_seeSessionPath: " + seeSessionPath);
        seeSession.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(TELLER_USER_NAME,TELLER_PASSWORD)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(seeSessionPath).get();
        assertEquals(200, seeSession.result().code());
        String recordStatus = seeSession.entities().item().get("RecordStatus");
        assertEquals("IHLD", recordStatus);
        System.out.println("");
    }
    
    // This test case is creating a temporary teller transaction, is putting it on hold and then is deleting it
    @Test
    public void testTellerLcyCashwdl_CreateAndHoldAndDelete() {
        // create a temporary teller cashwdl transaction
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                .basicAuth(TELLER_USER_NAME,TELLER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verTeller_LcyCashwdls()/new").post();
        assertEquals(201, session.result().code());
        
        
        //retrieve the teller cashwdl transaction number created above
        String id = session.entities().item().get("TransactionNumber");
        System.out.println("testTellerLcyCashwdl_CreateAndHoldAndDelete_id: " + id);
        
        //fill the mandatory input fields and put the teller cachin transaction on hold into the database
        //Note: it is not mandatory to set the fields but it seems useless to create an empty record (being set to IHLD the validations are not triggered)
        session.reuse()
                .set("verTeller_LcyCashwdl_Account1MvGroup(0)/AmountLocal1", "100")
                .set("Account2", "83267")
                .set("ValueDate2", "20170417")
                .set("verTeller_LcyCashwdl_DenominationMvGroup(0)/Unit", "1")
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/hold").url()
                .post();
        assertEquals(201, session.result().code());

        //check the value of the recordStatus, it must be IHLD
        InteractionSession seeSession = DefaultInteractionSession.newSession();
        String seeSessionPath = "verTeller_LcyCashwdls(" + id + ")/see";
        System.out.println("testTellerLcyCashwdl_CreateAndHoldAndDelete_seeSessionPath: " + seeSessionPath);
        seeSession.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(TELLER_USER_NAME,TELLER_PASSWORD)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(seeSessionPath).get();
        assertEquals(200, seeSession.result().code());
        String recordStatus = seeSession.entities().item().get("RecordStatus");
        System.out.println("testTellerLcyCashwdl_CreateAndHoldAndDelete_RecordStatus: " + recordStatus);
        assertEquals("IHLD", recordStatus);
        
        //open a new session for the teller cashwdl transaction that was put on hold and retrieve the Etag value
        InteractionSession delSession = DefaultInteractionSession.newSession();
        String delSessionPath = "verTeller_LcyCashwdls(" + id + ")";
        System.out.println("testTellerLcyCashwdl_CreateAndHoldAndDelete_delSessionPath: " + delSessionPath);
        delSession.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(TELLER_USER_NAME,TELLER_PASSWORD)
                    .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(delSessionPath)
                    .get();
        String delSessionEtag = delSession.header("ETag");
        System.out.println("testTellerLcyCashwdl_CreateAndHoldAndDelete_delSessionEtag: " + delSessionEtag);
        assertEquals(200, delSession.result().code());
        
        //delete customer in status IHLD
        //NOTE: The record is successfully deleted but the status code is 500 !!! --> to be checked this issue
        delSession.reuse()
                    .header(Configuration.HTTP_HEADER_IF_MATCH, delSessionEtag)
                    .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                    .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                    .entities().item().links()
                    .byRel("http://temenostech.temenos.com/rels/delete").url()
                    .delete();
        assertEquals(500, delSession.result().code());
        
        //check if indeed the teller cashwdl transaction is not present anymore into the database
        InteractionSession checkDelSession = DefaultInteractionSession.newSession();
        String checkDelSessionPath = "verTeller_LcyCashwdls(" + id + ")/see";
        System.out.println("testTellerLcyCashwdl_CreateAndHoldAndDelete_checkDelSessionPath: " + checkDelSessionPath);
        checkDelSession.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(TELLER_USER_NAME,TELLER_PASSWORD)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(checkDelSessionPath).get();
        assertEquals(404, checkDelSession.result().code());
        String resultCode = checkDelSession.entities().item().get("Errors_ErrorsMvGroup(0)/Code");
        assertEquals("RECORD MISSING", resultCode);
        System.out.println("");
    }

    // This test case will create a temporary teller cashwdl transaction, commit it into the database and then reverse it
    @Test
    public void testTellerLcyCashwdl_CreateAndUpdateAndAuthReverse() {
        // create a temporary teller cashwdl transaction
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                .basicAuth(TELLER_USER_NAME,TELLER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verTeller_LcyCashwdls()/new").post();
        assertEquals(201, session.result().code());
        
        //retrieve the teller cashwdl transaction number created above
        String id = session.entities().item().get("TransactionNumber");
        System.out.println("testTellerLcyCashwdl_CreateAndUpdateAndAuthReverse_id: " + id);
        
        //fill the mandatory input fields in order to commit the teller cashwdl transaction into the database
        session.reuse()
                .set("verTeller_LcyCashwdl_Account1MvGroup(0)/AmountLocal1", "100")
                .set("Account2", "83267")
                .set("ValueDate2", "20170417")
                .set("verTeller_LcyCashwdl_DenominationMvGroup(0)/Unit", "1")
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/input").url()
                .post();
        assertEquals(201, session.result().code());
        
        //retrieve the StatusRecord of the just created teller cashwdl transaction
        String recordStatusCreate = session.entities().item().get("RecordStatus");
        System.out.println("testTellerLcyCashwdl_CreateAndUpdateAndAuthReverse_RecordStatusCreate: " + recordStatusCreate);
        //retrieve the session ETag in order to be used down to the authorization step
        String sessionEtag = session.header("ETag");
        System.out.println("testTellerLcyCashwdl_CreateAndUpdateAndAuthReverse_sessionEtag: " + sessionEtag);
        
        //Authorize the creation of the teller cashwdl transaction into the database
        session.reuse()
                .header(Configuration.HTTP_HEADER_IF_MATCH, sessionEtag)
                .basicAuth(Configuration.AUTHORISER_USER_NAME, Configuration.AUTHORISER_PASSWORD).links()
                .byRel("http://temenostech.temenos.com/rels/authorise").url()
                .put();
        assertEquals(200, session.result().code());

        //check if the teller cashwdl transaction just created is indeed present into the database and retrieve the Etag value
        InteractionSession reverseSession = DefaultInteractionSession.newSession();
        String checkPath = "verTeller_LcyCashwdls(" + id + ")";
        System.out.println("testTellerLcyCashwdl_CreateAndUpdateAndAuthReverse_reversePath: " + checkPath);
        reverseSession.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(TELLER_USER_NAME,TELLER_PASSWORD)
                    .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(checkPath)
                    .get();
        String reverseSessionEtag = reverseSession.header("ETag");
        System.out.println("testTellerLcyCashwdl_CreateAndUpdateAndAuthReverse_reverseSessionEtag: " + reverseSessionEtag);
        assertEquals(200, reverseSession.result().code());
        
        //reverse the teller cashwdl transaction
        reverseSession.reuse()
                .header(Configuration.HTTP_HEADER_IF_MATCH, reverseSessionEtag)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/reverse").url()
                .put();
        assertEquals(200, reverseSession.result().code());
        //retrieve the ETag value in order to be used to the authorization step below
        String revInauSessionEtag = reverseSession.header("ETag");
        System.out.println("testTellerLcyCashwdl_CreateAndUpdateAndAuthReverse_revInauSessionEtag: " + revInauSessionEtag);
        
                //check the value of the recordStatus for the teller cashwdl transaction that was just reversed, it must be RNAU
        InteractionSession seeRnauSession = DefaultInteractionSession.newSession();
        String seeRnauSessionPath = "verTeller_LcyCashwdls(" + id + ")/see";
        System.out.println("testTellerLcyCashwdl_CreateAndUpdateAndAuthReverse_seeSessionPath: " + seeRnauSessionPath);
        seeRnauSession.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(TELLER_USER_NAME,TELLER_PASSWORD)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(seeRnauSessionPath).get();
        assertEquals(200, seeRnauSession.result().code());
        String recordStatusRev = seeRnauSession.entities().item().get("RecordStatus");
        System.out.println("testTellerLcyCashwdl_CreateAndUpdateAndAuthReverse_RecordStatusReverse: " + recordStatusRev);
        assertEquals("RNAU", recordStatusRev);
        
        //Authorize the reversal action of the teller cashwdl transaction into the database
        reverseSession.reuse()
                .header(Configuration.HTTP_HEADER_IF_MATCH, revInauSessionEtag)
                .basicAuth(Configuration.AUTHORISER_USER_NAME, Configuration.AUTHORISER_PASSWORD).links()
                .byRel("http://temenostech.temenos.com/rels/authorise").url()
                .put();
        assertEquals(200, reverseSession.result().code());
        
        //check the value of the recordStatus for the teller cashwdl transaction that was just authorized the reversal action, it must be REVE
        InteractionSession seeSession = DefaultInteractionSession.newSession();
        String seeSessionPath = "verTeller_LcyCashwdls(" + id + ")/see";
        System.out.println("testTellerLcyCashwdl_CreateAndUpdateAndAuthReverse_seeSessionPath: " + seeSessionPath);
        seeSession.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(TELLER_USER_NAME,TELLER_PASSWORD)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(seeSessionPath).get();
        assertEquals(200, seeSession.result().code());
        String recordStatus = seeSession.entities().item().get("RecordStatus");
        System.out.println("testTellerLcyCashwdl_CreateAndUpdateAndAuthReverse_RecordStatus: " + recordStatus);
        assertEquals("REVE", recordStatus);
        System.out.println("");
        
    }

    // This test case will create a temporary teller cashwdl transaction, commit it into the database and then delete it
    @Test
    public void testTellerLcyCashwdl_CreateAndUpdateAndUnauthReverse() {
        // create a temporary teller cashwdl transaction
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                .basicAuth(TELLER_USER_NAME,TELLER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verTeller_LcyCashwdls()/new").post();
        assertEquals(201, session.result().code());
        
        //retrieve the teller cashwdl transaction number created above
        String id = session.entities().item().get("TransactionNumber");
        System.out.println("testTellerLcyCashwdl_CreateAndUpdateAndUnauthReverse_id: " + id);
        
        //fill the mandatory input fields in order to commit the teller cashwdl transaction into the database
        session.reuse()
                .set("verTeller_LcyCashwdl_Account1MvGroup(0)/AmountLocal1", "100")
                .set("Account2", "83267")
                .set("ValueDate2", "20170417")
                .set("verTeller_LcyCashwdl_DenominationMvGroup(0)/Unit", "1")
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/input").url()
                .post();
        assertEquals(201, session.result().code());

        //retrieve the StatusRecord of the just created teller cashwdl transaction
        String recordStatusCreate = session.entities().item().get("RecordStatus");
        System.out.println("testTellerLcyCashwdl_CreateAndUpdateAndUnauthReverse_RecordStatusCreate: " + recordStatusCreate);
        //retrieve the session ETag in order to be used down to the authorization step
        String sessionEtag = session.header("ETag");
        System.out.println("testTellerLcyCashwdl_CreateAndUpdateAndUnauthReverse_sessionEtag: " + sessionEtag);
        
        //Authorize the creation of the teller cashwdl transaction into the database
        session.reuse()
                .header(Configuration.HTTP_HEADER_IF_MATCH, sessionEtag)
                .basicAuth(Configuration.AUTHORISER_USER_NAME, Configuration.AUTHORISER_PASSWORD).links()
                .byRel("http://temenostech.temenos.com/rels/authorise").url()
                .put();
        assertEquals(200, session.result().code());

        //check if the teller cashwdl transaction just created is indeed present into the database and retrieve the Etag value
        InteractionSession deleteSession = DefaultInteractionSession.newSession();
        String checkPath = "verTeller_LcyCashwdls(" + id + ")";
        System.out.println("testTellerLcyCashwdl_CreateAndUpdateAndUnauthReverse_deletePath: " + checkPath);
        deleteSession.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(TELLER_USER_NAME,TELLER_PASSWORD)
                    .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(checkPath)
                    .get();
        String reverseSessionEtag = deleteSession.header("ETag");
        System.out.println("testTellerLcyCashwdl_CreateAndUpdateAndUnauthReverse_deleteSessionEtag: " + reverseSessionEtag);
        assertEquals(200, deleteSession.result().code());
        
        //reverse the teller cashwdl transaction
        deleteSession.reuse()
                .header(Configuration.HTTP_HEADER_IF_MATCH, reverseSessionEtag)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/reverse").url()
                .put();
        assertEquals(200, deleteSession.result().code());
        
        //retrieve the ETag value in order to be used to the authorization step below
        String delInauSessionEtag = deleteSession.header("ETag");
        System.out.println("testTellerLcyCashwdl_CreateAndUpdateAndUnauthReverse_delInauSessionEtag: " + delInauSessionEtag);
        
        //check the value of the recordStatus for the teller cashwdl transaction that was just reversed, it must be RNAU
        InteractionSession seeRnauSession = DefaultInteractionSession.newSession();
        String seeRnauSessionPath = "verTeller_LcyCashwdls(" + id + ")/see";
        System.out.println("testTellerLcyCashwdl_CreateAndUpdateAndUnauthReverse_seeSessionPath: " + seeRnauSessionPath);
        seeRnauSession.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(TELLER_USER_NAME,TELLER_PASSWORD)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(seeRnauSessionPath).get();
        assertEquals(200, seeRnauSession.result().code());
        String recordStatusRev = seeRnauSession.entities().item().get("RecordStatus");
        System.out.println("testTellerLcyCashwdl_CreateAndUpdateAndUnauthReverse_RecordStatusReverse: " + recordStatusRev);
        assertEquals("RNAU", recordStatusRev);
        
        //Do not authorize the reversal action of the teller cashwdl transaction into the database
        deleteSession.reuse()
                .header(Configuration.HTTP_HEADER_IF_MATCH, delInauSessionEtag)
                .basicAuth(Configuration.AUTHORISER_USER_NAME, Configuration.AUTHORISER_PASSWORD).links()
                .byRel("http://temenostech.temenos.com/rels/delete").url()
                .delete();
        assertEquals(500, deleteSession.result().code());
        
        //check the value of the recordStatus for the teller cashwdl transaction that was just reversed, it must be empty
        InteractionSession seeSession = DefaultInteractionSession.newSession();
        String seeSessionPath = "verTeller_LcyCashwdls(" + id + ")/see";
        System.out.println("testTellerLcyCashwdl_CreateAndUpdateAndUnauthReverse_seeSessionPath: " + seeSessionPath);
        seeSession.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(TELLER_USER_NAME,TELLER_PASSWORD)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(seeSessionPath).get();
        assertEquals(200, seeSession.result().code());
        String recordStatus = seeSession.entities().item().get("RecordStatus");
        System.out.println("testTellerLcyCashwdl_CreateAndUpdateAndUnauthReverse_RecordStatus: " + recordStatus);
        assertEquals("", recordStatus);
        System.out.println("");
        
    }
}
