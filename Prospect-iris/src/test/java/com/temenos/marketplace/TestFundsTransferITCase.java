package com.temenos.marketplace;

import static org.junit.Assert.*;

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
public class TestFundsTransferITCase {

    // This test case will retrieve all available FT transactions
    @Test
    public void testFundsTransfer_RetrieveTxns() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verFundsTransfers()").get();
        assertEquals(200, session.result().code());
    }
    
    // This test case will retrieve the details of a FT transaction
    @Test
    public void testFundsTransfer_RetrieveDetailsSpecificTxn() {
        // retrieve all existing FT transactions
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verFundsTransfers()").get();
        assertEquals(200, session.result().code());
        
        //retrieve the id of the first FT transaction returned
        String id = session.entities().collection().get(0).get("RefNo");
        System.out.println("testFundsTransfer_RetrieveDetailsSpecificTxn_id: " + id);
        
        // retrieve the details of the specific FT transaction
        String specificPath = "verFundsTransfers(" + id + ")";
        InteractionSession session2 = DefaultInteractionSession.newSession();
        session2.basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
        .url()
        .baseuri(Configuration.DATA_SERVICE_URL)
        .path(specificPath).get();
        assertEquals(200, session.result().code());
        
        System.out.println("");
    }
    
    // This test case will fail because the request entity is in a format not supported by the requested resource
    @Test
    public void testFundsTransfer_CreateFailure() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verFundsTransfers()/new").post();
        assertEquals(415, session.result().code());
    }
    
    // This test case will be successful and it will create a temporary FT transaction
    @Test
    public void testFundsTransfer_Create() {
        // create an FT transaction
        InteractionSession sessionC = DefaultInteractionSession.newSession();
        sessionC.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verFundsTransfers()/new").post();
        assertEquals(201, sessionC.result().code());
    }
    
    // This test case will be successful and it will create a temporary FT transaction that is committed into the database also
    @Test
    public void testFundsTransfer_CreateAndUpdate() {
        // create an FT transaction
        InteractionSession sessionC = DefaultInteractionSession.newSession();
        sessionC.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verFundsTransfers()/new").post();
        assertEquals(201, sessionC.result().code());
        
        //retrieve the FT id just created above
        String id = sessionC.entities().item().get("RefNo");
        System.out.println("testFundsTransfer_id: " + id);
        
        //fill the mandatory input fields in order to save the customer into the database
        sessionC.reuse()
                .set("TransactionType", "AC")
                .set("CreditAmount", "5")
                .set("CreditCurrency", "USD")
                .set("DebitAcctNo", "75523")
                .set("CreditAcctNo", "74427")
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/input").url()
                .post();
        
        // in case of error returned then retrieve the error code and error info
        if (sessionC.result().code() != 201) {
            String resultErrorDel = sessionC.reuse().links()
                                            .byRel("http://temenostech.temenos.com/rels/errors")
                                            .embedded().entity().get("Errors_ErrorsMvGroup(0)/Code");
            System.out.println("testFundsTransfer_CreateAndUpdate_error: " + resultErrorDel);

            String resultInfoErrorDel = sessionC.reuse().links()
                                                        .byRel("http://temenostech.temenos.com/rels/errors")
                                                        .embedded().entity().get("Errors_ErrorsMvGroup(0)/Info");
            System.out.println("testFundsTransfer_CreateAndUpdate_InfoError: " + resultInfoErrorDel);           
        }
        
        assertEquals(201, sessionC.result().code());
    }

    // This test case is creating a temporary teller transaction and then is putting it on hold
    @Test
    public void testFundsTransfer_CreateAndHold() {
        // create a temporary FT transaction
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verFundsTransfers()/new").post();
        assertEquals(201, session.result().code());
        
        //retrieve the FT transaction number created above
        String id = session.entities().item().get("RefNo");
        System.out.println("testFundsTransfer_CreateAndHold_id: " + id);
        
        //fill the mandatory input fields and put the FT transaction on hold into the database
        //Note: it is not mandatory to set the fields but it seems useless to create an empty record (being set to IHLD the validations are not triggered)
        session.reuse()
                .set("TransactionType", "AC")
                .set("CreditAmount", "5")
                .set("CreditCurrency", "USD")
                .set("DebitAcctNo", "75523")
                .set("CreditAcctNo", "74427")
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/hold").url()
                .post();
        assertEquals(201, session.result().code());

        //check the value of the recordStatus, it must be IHLD
        InteractionSession seeSession = DefaultInteractionSession.newSession();
        String seeSessionPath = "verFundsTransfers(" + id + ")/see";
        System.out.println("testFundsTransfer_CreateAndHold_seeSessionPath: " + seeSessionPath);
        seeSession.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(seeSessionPath).get();
        assertEquals(200, seeSession.result().code());
        String recordStatus = seeSession.entities().item().get("RecordStatus");
        assertEquals("IHLD", recordStatus);
        System.out.println("");
    }
    
    // This test case is creating a temporary FT transaction, is putting it on hold  and then it will delete it
    @Test
    public void testFundsTransfer_CreateAndHoldAndDelete() {
        // create a temporary FT transaction
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verFundsTransfers()/new").post();
        assertEquals(201, session.result().code());
        
        
        //retrieve the teller FT transaction number created above
        String id = session.entities().item().get("RefNo");
        System.out.println("testFundsTransfer_CreateAndHoldAndDelete_id: " + id);
        
        //fill the mandatory input fields and put the FT transaction on hold into the database
        //Note: it is not mandatory to set the fields but it seems useless to create an empty record (being set to IHLD the validations are not triggered)
        session.reuse()
                .set("TransactionType", "AC")
                .set("CreditAmount", "5")
                .set("CreditCurrency", "USD")
                .set("DebitAcctNo", "75523")
                .set("CreditAcctNo", "74427")
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/hold").url()
                .post();
        assertEquals(201, session.result().code());

        //check the value of the recordStatus, it must be IHLD
        InteractionSession seeSession = DefaultInteractionSession.newSession();
        String seeSessionPath = "verFundsTransfers(" + id + ")/see";
        System.out.println("testFundsTransfer_CreateAndHoldAndDelete_seeSessionPath: " + seeSessionPath);
        seeSession.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(seeSessionPath).get();
        assertEquals(200, seeSession.result().code());
        String recordStatus = seeSession.entities().item().get("RecordStatus");
        System.out.println("testFundsTransfer_CreateAndHoldAndDelete_RecordStatus: " + recordStatus);
        assertEquals("IHLD", recordStatus);
        
        //open a new session for the FT transaction that was put on hold and retrieve the Etag value
        InteractionSession delSession = DefaultInteractionSession.newSession();
        String delSessionPath = "verFundsTransfers(" + id + ")";
        System.out.println("testFundsTransfer_CreateAndHoldAndDelete_delSessionPath: " + delSessionPath);
        delSession.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                    .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(delSessionPath)
                    .get();
        String delSessionEtag = delSession.header("ETag");
        System.out.println("testFundsTransfer_CreateAndHoldAndDelete_delSessionEtag: " + delSessionEtag);
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
        
        //check if indeed the FT transaction is not present anymore into the database
        InteractionSession checkDelSession = DefaultInteractionSession.newSession();
        String checkDelSessionPath = "verFundsTransfers(" + id + ")/see";
        System.out.println("testFundsTransfer_CreateAndHoldAndDelete_checkDelSessionPath: " + checkDelSessionPath);
        checkDelSession.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(checkDelSessionPath).get();
        assertEquals(404, checkDelSession.result().code());
        String resultCode = checkDelSession.entities().item().get("Errors_ErrorsMvGroup(0)/Code");
        assertEquals("RECORD MISSING", resultCode);
        System.out.println("");
    }
    
    // This test case will create a temporary FT transaction, commit it into the database and then reverse it
    @Test
    public void testFundsTransfer_CreateAndUpdateAndReverse() {
        // create a temporary FT transaction
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verFundsTransfers()/new").post();
        assertEquals(201, session.result().code());
        
        //retrieve the FT transaction number created above
        String id = session.entities().item().get("RefNo");
        System.out.println("testFundsTransfer_CreateAndUpdateAndReverse_id: " + id);
        
        //fill the mandatory input fields in order to commit the FT transaction into the database
        session.reuse()
                .set("TransactionType", "AC")
                .set("CreditAmount", "5")
                .set("CreditCurrency", "USD")
                .set("DebitAcctNo", "75523")
                .set("CreditAcctNo", "74427")
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/input").url()
                .post();
        assertEquals(201, session.result().code());

        //check if the FT transaction just created is indeed present into the database and retrieve the Etag value
        InteractionSession reverseSession = DefaultInteractionSession.newSession();
        String checkPath = "verFundsTransfers(" + id + ")";
        System.out.println("ttestFundsTransfer_CreateAndUpdateAndReverse_reversePath: " + checkPath);
        reverseSession.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                    .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(checkPath)
                    .get();
        String reverseSessionEtag = reverseSession.header("ETag");
        System.out.println("testFundsTransfer_CreateAndUpdateAndReverse_reverseSessionEtag: " + reverseSessionEtag);
        assertEquals(200, reverseSession.result().code());
        
        //reverse the FT transaction
        reverseSession.reuse()
                .header(Configuration.HTTP_HEADER_IF_MATCH, reverseSessionEtag)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/reverse").url()
                .put();
        assertEquals(200, reverseSession.result().code());
        
        //check the value of the recordStatus for the FT transaction that was just reversed, it must be REVE
        InteractionSession seeSession = DefaultInteractionSession.newSession();
        String seeSessionPath = "verFundsTransfers(" + id + ")/see";
        System.out.println("testFundsTransfer_CreateAndUpdateAndReverse_seeSessionPath: " + seeSessionPath);
        seeSession.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(seeSessionPath).get();
        assertEquals(200, seeSession.result().code());
        String recordStatus = seeSession.entities().item().get("RecordStatus");
        System.out.println("testFundsTransfer_CreateAndUpdateAndReverse_RecordStatus: " + recordStatus);
        assertEquals("REVE", recordStatus);
        
        System.out.println("");
        
    }
    
    // This test case is creating a temporary FT transaction, it is successfully validating it and then it is committing it into the database
    @Test
    public void testFundsTransfer_CreateAndValidateAndUpdateSuccess() {
        // create a temporary FT transaction
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verFundsTransfers()/new").post();
        assertEquals(201, session.result().code());
        
        //retrieve the FT transaction number created above
        String id = session.entities().item().get("RefNo");
        System.out.println("testFundsTransfer_CreateAndValidateAndUpdateSuccess_id: " + id);
        
        //fill the mandatory input fields in order to validate the FT transaction
        session.reuse()
                .set("TransactionType", "AC")
                .set("CreditAmount", "5")
                .set("CreditCurrency", "USD")
                .set("DebitAcctNo", "75523")
                .set("CreditAcctNo", "74427")
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/validate").url()
                .post();
        assertEquals(200, session.result().code());
        
      //commit the FT transaction that was validated above into the database
        session.reuse()
        .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/input").url()
                .post();
        assertEquals(201, session.result().code());
        System.out.println("");
    }
    
    // This test case is creating a temporary FT transaction and then is validating it without success
    @Test
    public void testFundsTransfer_CreateAndValidateFailure() {
        // create a temporary FT transaction
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verFundsTransfers()/new").post();
        assertEquals(201, session.result().code());
        
        //retrieve the FT transaction number created above
        String id = session.entities().item().get("TransactionNumber");
        System.out.println("testFundsTransfer_CreateAndValidateFailure_id: " + id);
        
        //validate the FT created by not filling the mandatory input fields
        session.reuse()
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/validate").url()
                .post();
        assertEquals(400, session.result().code());
        //retrieve the error code returned
        
        String resultErrorValidate = session.reuse().links()
                                            .byRel("http://temenostech.temenos.com/rels/errors")
                                            .embedded().entity().get("Errors_ErrorsMvGroup(0)/Code");
        System.out.println("testFundsTransfer_CreateAndValidateFailure_error: " + resultErrorValidate);
        String stringToMatch = "((.*)INPUT MISSING(.*))";
        assertTrue(resultErrorValidate.matches(stringToMatch));
        
        System.out.println("");
    }
    
    // This test case is creating a temporary FT transaction and then is successfully validating it
    @Test
    public void testFundsTransfer_CreateAndValidateSuccess() {
        // create a temporary FT transaction
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verFundsTransfers()/new").post();
        assertEquals(201, session.result().code());
        
        //retrieve the FT transaction number created above
        String id = session.entities().item().get("RefNo");
        System.out.println("testFundsTransfer_CreateAndValidateSuccess_id: " + id);
        
        //fill the mandatory input fields in order to commit the FT transaction into the database
        session.reuse()
                .set("TransactionType", "AC")
                .set("CreditAmount", "5")
                .set("CreditCurrency", "USD")
                .set("DebitAcctNo", "75523")
                .set("CreditAcctNo", "74427")
                .entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/validate").url()
                .post();
        assertEquals(200, session.result().code());
        System.out.println("");
    }
    
}
