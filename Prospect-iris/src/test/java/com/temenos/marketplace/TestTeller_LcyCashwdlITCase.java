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
}
