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
public class TestTeller_LcyCashinCashwdlITCase {

    //the user below is a teller one that should be present into the testing area, otherwise it should be changed with another one
    
    // This test case is going to:
    //     1. add money into an account by creating a transaction
    //     2. check if the cashin transaction is present into the database
    //     3. withdraw money from that account by creating a transaction
    //     4. check if the cashwdl transaction is present into the database
    @Test
    public void testTellerLcyCashinCashwdlSuccess() throws UnsupportedEncodingException {
        // create a temporary teller cashin transaction
        InteractionSession CashinSession = DefaultInteractionSession.newSession();
        CashinSession.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                       .basicAuth(Configuration.TELLER_USER_NAME,Configuration.TELLER_PASSWORD)
                       .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                       .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                       .url()
                       .baseuri(Configuration.DATA_SERVICE_URL)
                       .path("verTeller_LcyCashins()/new").post();
        assertEquals(201, CashinSession.result().code());
        
        //retrieve the teller cashin transaction number created above
        String id = CashinSession.entities().item().get("TransactionNumber");
        System.out.println("testTellerLcyCashinCashwdlSuccess_CashinID: " + id);
        
        //fill the mandatory input fields in order to commit the teller cashin transaction into the database
        CashinSession.reuse()
                       .set("verTeller_LcyCashin_Account1MvGroup(0)/AmountLocal1", "100")
                       .set("Account2", "83267")
                       .set("verTeller_LcyCashin_DrDenomMvGroup(0)/DrUnit", "1")
                       .entities().item().links()
                       .byRel("http://temenostech.temenos.com/rels/input").url()
                       .post();
        // in case of error returned then retrieve the error code and error info
        if (CashinSession.result().code() != 201) {
            String resultCashinCodeError = CashinSession.reuse().links()
                                                  .byRel("http://temenostech.temenos.com/rels/errors")
                                                  .embedded().entity().get("Errors_ErrorsMvGroup(0)/Code");
            System.out.println("testTellerLcyCashinCashwdlSuccess_CashinCodeError: " + resultCashinCodeError);

            String resultCashinInfoError = CashinSession.reuse().links()
                    .byRel("http://temenostech.temenos.com/rels/errors")
                    .embedded().entity().get("Errors_ErrorsMvGroup(0)/Info");
            System.out.println("testTellerLcyCashinCashwdlSuccess_CashinInfoError: " + resultCashinInfoError);
        }
        assertEquals(201, CashinSession.result().code());

        //check if the teller cashin transaction just created is indeed present into the database
        InteractionSession sessionCashinCheck = DefaultInteractionSession.newSession();
        String intermediateCashinPath = "$filter=TransactionNumber eq '" + id + "'";
        String checkCashinPath = "verTeller_LcyCashins()?" + URLEncoder.encode(intermediateCashinPath, "UTF-8");
        System.out.println("testTellerLcyCashinCashwdlSuccess_checkCashinPath: " + checkCashinPath);
        sessionCashinCheck.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                            .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                            .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                            .url()
                            .baseuri(Configuration.DATA_SERVICE_URL)
                            .path(checkCashinPath).get();
        assertEquals(200, sessionCashinCheck.result().code());
        System.out.println("");
        
        // create a temporary teller cashwdl transaction
        InteractionSession cashwdlSession = DefaultInteractionSession.newSession();
        cashwdlSession.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                       .basicAuth(Configuration.TELLER_USER_NAME,Configuration.TELLER_PASSWORD)
                       .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                       .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                       .url()
                       .baseuri(Configuration.DATA_SERVICE_URL)
                       .path("verTeller_LcyCashwdls()/new").post();
        assertEquals(201, cashwdlSession.result().code());
        
        //retrieve the teller cashwdl transaction number created above
        String CashwdlID = cashwdlSession.entities().item().get("TransactionNumber");
        System.out.println("testTellerLcyCashinCashwdlSuccess_CashwdlID: " + CashwdlID);
        
        //fill the mandatory input fields in order to commit the teller cashwdl transaction into the database
        cashwdlSession.reuse()
                        .set("verTeller_LcyCashwdl_Account1MvGroup(0)/AmountLocal1", "100")
                        .set("Account2", "83267")
                        .set("verTeller_LcyCashwdl_DenominationMvGroup(0)/Unit", "1")
                        .entities().item().links()
                        .byRel("http://temenostech.temenos.com/rels/input").url()
                        .post();
        // in case of error returned then retrieve the error code and error info
        if (cashwdlSession.result().code() != 201) {
            String resultCashinCodeError = cashwdlSession.reuse().links()
                                                         .byRel("http://temenostech.temenos.com/rels/errors")
                                                         .embedded().entity().get("Errors_ErrorsMvGroup(0)/Code");
            System.out.println("testTellerLcyCashinCashwdlSuccess_CashinCodeError: " + resultCashinCodeError);

            String resultCashinInfoError = cashwdlSession.reuse().links()
                                                         .byRel("http://temenostech.temenos.com/rels/errors")
                                                         .embedded().entity().get("Errors_ErrorsMvGroup(0)/Info");
            System.out.println("testTellerLcyCashinCashwdlSuccess_CashinInfoError: " + resultCashinInfoError);           
        }
        assertEquals(201, cashwdlSession.result().code());

        //retrieve the StatusRecord of the just created teller cashwdl transaction
        String recordStatusCreate = cashwdlSession.entities().item().get("RecordStatus");
        System.out.println("testTellerLcyCashinCashwdlSuccess_RecordStatusCreate: " + recordStatusCreate);
        //retrieve the session ETag in order to be used down to the authorization step
        String sessionEtag = cashwdlSession.header("ETag");
        System.out.println("testTellerLcyCashinCashwdlSuccess_sessionEtag: " + sessionEtag);
        
        //Authorize the creation of the teller cashwdl transaction into the database
        cashwdlSession.reuse()
                .header(Configuration.HTTP_HEADER_IF_MATCH, sessionEtag)
                .basicAuth(Configuration.AUTHORISER_USER_NAME, Configuration.AUTHORISER_PASSWORD).links()
                .byRel("http://temenostech.temenos.com/rels/authorise").url()
                .put();
        assertEquals(200, cashwdlSession.result().code());
        
        //check if the teller cashwdl transaction just created is indeed present into the database
        InteractionSession sessionCashwdlCheck = DefaultInteractionSession.newSession();
        String intermediateCashwdlPath = "$filter=TransactionNumber eq '" + id + "'";
        String checkCashwdlPath = "verTeller_LcyCashwdls()?" + URLEncoder.encode(intermediateCashwdlPath, "UTF-8");
        System.out.println("testTellerLcyCashinCashwdlSuccess_checkCashwdlPath: " + checkCashwdlPath);
        sessionCashwdlCheck.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                            .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                            .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                            .url()
                            .baseuri(Configuration.DATA_SERVICE_URL)
                            .path(checkCashwdlPath).get();
        assertEquals(200, sessionCashwdlCheck.result().code());
        System.out.println("");
    }
}
