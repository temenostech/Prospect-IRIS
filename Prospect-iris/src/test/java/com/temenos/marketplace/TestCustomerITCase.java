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

    // This test case will create a temporary customer and it will update it with the mandatory fields and commit it to the database
    @Test
    public void testCreateNewCustomerAndUpdate() {
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
        System.out.println("id: " + id);
        
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
        //String localPath = "verCustomers()?$filter=CustomerCode eq '" + id + "'";
        //String localPath = "verCustomers()?$filter=substringof('" + id + "', CustomerCode)";
        String localPath = "verCustomers(" + id + ")";
        System.out.println("localPath: " + localPath);
        sessionCheck.registerHandler(Configuration.APPLICATION_ATOM_XML, AtomPayloadHandler.class)
                    .basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
                    .header(Configuration.HTTP_HEADER_ACCEPT, Configuration.APPLICATION_ATOM_XML)
                    .url()
                    .baseuri(Configuration.DATA_SERVICE_URL)
                    .path(localPath).get();
        assertEquals(200, sessionCheck.result().code());
        
        //delete the record from the database
        
    }
   
}
