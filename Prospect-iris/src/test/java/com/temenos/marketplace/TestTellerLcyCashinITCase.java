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

public class TestTellerLcyCashinITCase {
    
    public static String TELLER_USER_NAME = "CSAGENT";
    public static String TELLER_PASSWORD = "123456";
    
    // This test case will fail because the request entity is in a format not supported by the requested resource
    @Test
    public void testTellerLcyCashin1_createF1() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verTeller_LcyCashins()/new").post();
        
        System.out.println("testTellerLcyCashin1_createF1_status: " + session.result().code());
        assertEquals(415, session.result().code());
    }
    
 // This test case will fail because the user used is not having the TILL opened
    @Test
    public void testTellerLcyCashin1_createF2() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verTeller_LcyCashins()/new").post();
        
        System.out.println("testTellerLcyCashin1_createF2_status: " + session.result().code());
        assertEquals(400, session.result().code());
        
        String resultError = session.entities().item().get("Errors_ErrorsMvGroup");
        String resultError2 = session.entities().item().get("Errors_ErrorsMvGroup").toString().trim();
        System.out.println("testTellerLcyCashin1_createF2_error: " + resultError);
        System.out.println("testTellerLcyCashin1_createF2_error2: " + resultError2);
        String stringToMatch = "((.*)TILL NOT OPEN(.*))";
        String stringToMatch2 = "TILL NOT OPEN";
        //String stringToMatch ="((.*)\\<d\\:Text\\>TILL NOT OPEN\\<\\/d\\:Text\\>(.*))";
        System.out.println("testTellerLcyCashin1_create_errorMatch: " + stringToMatch);
        System.out.println("testTellerLcyCashin1_create_errorMatch2: " + stringToMatch2);
        //assertTrue(resultError.matches(stringToMatch));
        //assertTrue(resultError2.matches(stringToMatch2));
    }
   
    // This test case will be successful and it will create a temporary teller transaction
    @Test
    public void testTellerLcyCashin2_createS() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler("application/atom+xml",AtomPayloadHandler.class)
                .basicAuth(TELLER_USER_NAME,TELLER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verTeller_LcyCashins()/new").post();
        
        System.out.println("testTellerLcyCashin2_createS_status: " + session.result().code());
        assertEquals(201, session.result().code());
    }

}
