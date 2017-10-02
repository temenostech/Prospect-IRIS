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
public class TestStmtAccountStatementITCase {

    @Test
    public void testRetrieveStmtAccountStatement() throws IOException {   
        //form the path request for the STMT.ACCOUNT.STATEMENT enquiry with an account that has transactions
        String enqtestRetrieveStmtAccountStatementsPath = "enqAccountStatements()?$filter=StatementId%20eq%20'76414'";
        System.out.println("testRetrieveStmtAccountStatement_path: " + enqtestRetrieveStmtAccountStatementsPath);
        
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(enqtestRetrieveStmtAccountStatementsPath).get();
        
        assertEquals(200, session.result().code());
        
    }

    @Test
    public void testNoStmtFound() throws IOException {
      //form the path request for the STMT.ACCOUNT.STATEMENT enquiry with an account that doesn't have transactions
        String enqtestRetrieveStmtAccountStatementsPath = "enqAccountStatements()?$filter=StatementId%20eq%20'11011'";
        System.out.println("testRetrieveStmtAccountStatement_path: " + enqtestRetrieveStmtAccountStatementsPath);
        
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(enqtestRetrieveStmtAccountStatementsPath).get();
        
        assertEquals(200, session.result().code());
    }

}
