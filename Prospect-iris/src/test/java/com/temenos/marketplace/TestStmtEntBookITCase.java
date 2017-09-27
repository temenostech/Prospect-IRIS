package com.temenos.marketplace;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.temenos.useragent.generic.DefaultInteractionSession;
import com.temenos.useragent.generic.InteractionSession;

/**
 * TODO: Document me!
 *
 * @author jfiricel
 *
 */
public class TestStmtEntBookITCase {

    @Test
    public void testRetrieveStmtEntBook() throws IOException {    
        //form the path request for the STMT.ENQ.Book enquiry with an account for which Book transactions will be retrieved
        String enqStmtEntBooksPath = "enqStmtEntBooks()?$filter=AcctId%20eq%20'74306'%20and%20BookingDate%20eq%2020170417";
        System.out.println("testRetrieveStmtEntBook_path: " + enqStmtEntBooksPath);
        
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(enqStmtEntBooksPath).get();
        
        assertEquals(200, session.result().code());
        
    }

    @Test
    public void testRetrievePeriodStmtEntBook() throws IOException {    
        //form the path request for the STMT.ENQ.Book enquiry with an account for which Book transactions will be retrieved during a period time
        String enqStmtEntBooksPath = "enqStmtEntBooks()?$filter=AcctId%20eq%20'74306'%20and%20BookingDate%20gt%2020170417%20and%20BookingDate%20lt%2020170417";
        System.out.println("testRetrievePeriodStmtEntBook_path: " + enqStmtEntBooksPath);
        
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(enqStmtEntBooksPath).get();
        
        assertEquals(200, session.result().code());
        
    }

    @Test
    public void testNoStmtBookFound() throws IOException {
        //form the path request for the STMT.ENQ.Book enquiry with an account that didn't do any transaction recently
        String enqStmtEntBooksPath = "enqStmtEntBooks()?$filter=AcctId%20eq%20'78131'%20and%20BookingDate%20eq%2020170419";
        System.out.println("testNoStmtBookFound_path: " + enqStmtEntBooksPath);
        
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(enqStmtEntBooksPath).get();
        
        assertEquals(200, session.result().code());
    }

}
