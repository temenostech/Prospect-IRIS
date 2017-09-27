package com.temenos.marketplace;

import static org.junit.Assert.*;

import org.junit.Test;

import com.temenos.useragent.generic.DefaultInteractionSession;
import com.temenos.useragent.generic.InteractionSession;

/**
 * TODO: Document me!
 *
 * @author jfiricel
 *
 */
public class TestFtTxnTypeConditionITCase {

    @Test
    public void testRetrieveTypeCondRecords() {
       // retrieve all the records returned by the enquiry
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("enqFtTxnTypeConditions").get();
        assertEquals(200, session.result().code());
    }

    @Test
    public void testRetrieveTypeCondSpecificRecords() {
       // retrieve all the records returned by the enquiry
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path("enqFtTxnTypeConditions").get();
        assertEquals(200, session.result().code());
        
       //retrieve the id of the first record returned
        String id = session.entities().collection().get(0).get("TransactionType");
        System.out.println("testRetrieveTypeCondSpecificRecords_id: " + id);
       
       // retrieve the details of the specific record
        String specificPath = "enqFtTxnTypeConditions(" + id + ")";
        InteractionSession session2 = DefaultInteractionSession.newSession();
        session2.basicAuth(Configuration.INPUTTER_USER_NAME,Configuration.INPUTTER_PASSWORD)
        .url()
        .baseuri(Configuration.DATA_SERVICE_URL)
        .path(specificPath).get();
        assertEquals(200, session.result().code());
        
    }
}
