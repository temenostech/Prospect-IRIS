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
public class TestStmtEntTodayITCase {

    @Test
    public void testRetrieveStmtEntToday() throws IOException {    
        //form the path request for the STMT.ENQ.TODAY enquiry with an account that did today a transaction
        String enqStmtEntTodaysPath = "enqStmtEntTodays()?$filter=AcctId%20eq%20'78131'";
        System.out.println("testRetrieveStmtEntToday_path: " + enqStmtEntTodaysPath);
        
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(enqStmtEntTodaysPath).get();
        
        assertEquals(200, session.result().code());
        
    }

    @Test
    public void testNoStmtFound() throws IOException {
        //form the path request for the STMT.ENQ.TODAY enquiry with an account that doesn't did any transaction today
        String enqStmtEntTodaysPath = "enqStmtEntTodays()?$filter=AcctId%20eq%20'11011'";
        System.out.println("testNoStmtFound_path: " + enqStmtEntTodaysPath);
        
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(enqStmtEntTodaysPath).get();
        
        assertEquals(200, session.result().code());
    }
}
