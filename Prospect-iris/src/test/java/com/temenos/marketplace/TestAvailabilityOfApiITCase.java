package com.temenos.marketplace;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.temenos.useragent.generic.DefaultInteractionSession;
import com.temenos.useragent.generic.InteractionSession;


/**
 * Tests make sure these API must exist in Prospect-IRIS
 * 
 * @author mjangid
 *
 */
public class TestAvailabilityOfApiITCase {
 
    //Customer
    private String verCustomer_Inputs = "verCustomer_Inputs";
    private String verCustomer_Corps = "verCustomer_Corps";
    private String verCustomers = "verCustomers";
    private String enqCustomerLists = "enqCustomerLists";
    private String enqCustomerDetailss = "enqCustomerDetailss";
    
    //Account
    private String verAccount_CaOpens = "verAccount_CaOpens";
    private String verAccount_SbOpens = "verAccount_SbOpens";
    private String verAccounts = "verAccounts";
    private String enqPctAccounts = "enqPctAccounts";
    private String enqAccountDetailss = "enqAccountDetailss";
    private String enqAcctBals = "enqAcctBals";
    //private String enqCustAccts = "enqCustAccts";
    private String enqAccountStatements = "enqAccountStatements";
    private String enqAcctStmtHists = "enqAcctStmtHists";
    
    //Statement Entries
    //private String enqStmtEntTodays = "enqStmtEntTodays";
    //private String enqStmtEntLasts = "enqStmtEntLasts";
    //private String enqStmtEntBooks = "enqStmtEntBooks";
    private String enqAdminFtTxnTypeConditions = "enqAdminFtTxnTypeConditions";
    private String enqPctStmtEntrys = "enqPctStmtEntrys";
    
    //Users
    private String verUsers = "verUsers";
    private String verEbExternalUsers = "verEbExternalUsers";
    private String verEbExternalUser_TcibNews = "verEbExternalUser_TcibNews";
    
    //Debit Card
    private String verCardIssues = "verCardIssues";
    private String verCardIssue_Inputs = "verCardIssue_Inputs";
    
    //Funds Transfer
    private String verFundsTransfers = "verFundsTransfers";
    private String verFundsTransfer_Inputs = "verFundsTransfer_Inputs";
    
    //Teller
    private String verTeller_LcyCashins = "verTeller_LcyCashins";
    private String verTeller_LcyCashwdls = "verTeller_LcyCashwdls";
    private String verTeller_FcyCashins = "verTeller_FcyCashins";
    private String verTeller_LcyCashchqs = "verTeller_LcyCashchqs";
    private String verTeller_FcyCashchqs = "verTeller_FcyCashchqs";
    
    //Currency
    private String enqCurrencyLists = "enqCurrencyLists";
    
    @Test
    public void testEnqAccountStatements() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(enqAccountStatements).get();
        
        assertTrue(session.entities().isCollection());
        //assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testEnqCustomerLists() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(enqCustomerLists).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
     
    @Test
    public void testVerCustomers() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(verCustomers).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testVerAccounts() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(verAccounts).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testVerUsers() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(verUsers).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testVerEbExternalUsers() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(verEbExternalUsers).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testVerCardIssues() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(verCardIssues).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testVerFundsTransfers() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(verFundsTransfers).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testVerTeller_LcyCashins() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(verTeller_LcyCashins).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testVerTeller_LcyCashwdls() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(verTeller_LcyCashwdls).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testVerAccount_CaOpens() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(verAccount_CaOpens).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testVerAccount_SbOpens() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(verAccount_SbOpens).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testVerCustomer_Corps() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(verCustomer_Corps).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testVerFundsTransfer_Inputs() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(verFundsTransfer_Inputs).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testEnqAcctStmtHists() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(enqAcctStmtHists).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testEnqAdminFtTxnTypeConditions() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(enqAdminFtTxnTypeConditions).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testEnqStmtEntTodays() throws IOException {
        //The account that must be used here should have transactions done in today's day, otherwise the test will fail!
        String enqStmtEntTodaysPath = "enqStmtEntTodays()?$filter=AcctId%20eq%20'78239'";
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(enqStmtEntTodaysPath).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testEnqStmtEntLasts() {
        String enqStmtEntLastsPath = "enqStmtEntLasts()?$filter=AcctId%20eq%20'78131'";
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(enqStmtEntLastsPath).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testEnqStmtEntBooks() {
        String enqStmtEntBooksPath = "enqStmtEntBooks()?$filter=AcctId%20eq%20'74306'%20and%20BookingDate%20eq%2020170417";
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(enqStmtEntBooksPath).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testVerCustomer_Inputs() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(verCustomer_Inputs).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testEnqCustomerDetailss() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(enqCustomerDetailss).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testEnqPctAccounts() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(enqPctAccounts).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testEnqAccountDetailss() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(enqAccountDetailss).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testEnqAcctBals() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(enqAcctBals).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testEnqCustAccts() {
        //The customer that must be used here should have accounts, otherwise the test will fail!
        String enqCustAcctsPath = "enqCustAccts()?filter=Customer%20eq%20'190100'";
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(enqCustAcctsPath).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    public void testVerEbExternalUser_TcibNews() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(verEbExternalUser_TcibNews).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testVerCardIssue_Inputs() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(verCardIssue_Inputs).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testVerTeller_FcyCashins() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(verTeller_FcyCashins).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testVerTeller_LcyCashchqs() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(verTeller_LcyCashchqs).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testVerTeller_FcyCashchqs() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(verTeller_FcyCashchqs).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testEnqCurrencyLists() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(enqCurrencyLists).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
    @Test
    public void testEnqPctStmtEntrys() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(enqPctStmtEntrys).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
    
}