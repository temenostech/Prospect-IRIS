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
public class TestCustomerITCase {
/*
    // This test case will fail because the request entity is in a format not supported by the requested resource (Content-Type is not set)
    @Test
    public void testCreateF1() {
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
    public void testCreateS1() {
        InteractionSession sessionS1 = DefaultInteractionSession.newSession();
        sessionS1.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verCustomers()/new").post();
        assertEquals(201, sessionS1.result().code());
    }
*/
    // This test case will fail because of missing mandatory fields
    @Test
    public void testCreateF2_missingMandatoryInput() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                .header(Configuration.HTTP_HEADER_CONTENT_TYPE, Configuration.APPLICATION_ATOM_XML)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("verCustomers()/new").post();

        assertEquals(201, session.result().code());

        String id = session.entities().item().get("CustomerCode");
        System.out.println("id: " + id);
        
        session.reuse()
                .set("Mnemonic", "C" + id)
                .set("verCustomer_Name1MvGroup(0)/Name1", "Mr Robin Peterson")
                .set("verCustomer_ShortNameMvGroup(0)/ShortName", "Rob")
                .set("Language", "1")
                .set("Sector", "1001").set("Gender", "MALE").set("Title", "MR")
                .set("FamilyName", "Peterson" + id).entities().item().links()
                .byRel("http://temenostech.temenos.com/rels/input").url()
                .post();
        
        assertEquals(201, session.result().code());
        
        String resultError = session.entities().item().get("Errors_ErrorsMvGroup");
        System.out.println("testCustomer_createF2_error: " + resultError);
        
    }
}
