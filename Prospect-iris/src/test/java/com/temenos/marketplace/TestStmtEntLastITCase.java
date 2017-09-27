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
public class TestStmtEntLastITCase {

    @Test
    public void testRetrieveStmtEntLast() throws IOException {    
        //form the path request for the STMT.ENQ.LAST enquiry with an account for which last transactions will be retrieved
        String enqStmtEntLastsPath = "enqStmtEntLasts()?$filter=AcctId%20eq%20'78131'";
        System.out.println("testRetrieveStmtEntLast_path: " + enqStmtEntLastsPath);
        
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(enqStmtEntLastsPath).get();
        
        assertEquals(200, session.result().code());
        
    }

    @Test
    public void testNoStmtLastFound() throws IOException {
        //form the path request for the STMT.ENQ.LAST enquiry with an account that doesn't did any transaction recently
        String enqStmtEntLastsPath = "enqStmtEntLasts()?$filter=AcctId%20eq%20'11011'";
        System.out.println("testNoStmtLastFound_path: " + enqStmtEntLastsPath);
        
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(enqStmtEntLastsPath).get();
        
        assertEquals(200, session.result().code());
    }

}
