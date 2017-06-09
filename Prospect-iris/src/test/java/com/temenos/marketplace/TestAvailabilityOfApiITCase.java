package com.temenos.marketplace;

import static org.junit.Assert.*;

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
 
    private String enqAccountStatements = "enqAccountStatements";
    private String enqCustomerDetsScvs = "enqCustomerDetsScvs";
    private String enqCustomerLists = "enqCustomerLists";
    private String enqPctCustomerAccounts = "enqPctCustomerAccounts";
    private String verCustomers = "verCustomers";
    private String verAccounts = "verAccounts";
    private String verUsers = "verUsers";
    private String verEbExternalUsers = "verEbExternalUsers";
    private String verCardIssues = "verCardIssues";
    private String verFundsTransfers = "verFundsTransfers";
    private String verTeller_LcyCashins = "verTeller_LcyCashins";
    private String verTeller_LcyCashwdls = "verTeller_LcyCashwdls";
    
    private String verAccount_CaOpens = "verAccount_CaOpens";
    private String verAccount_SbOpens = "verAccount_SbOpens";
    private String verCustomer_Inputs = "verCustomer_Inputs";
    private String verFundsTransfer_Inputs = "verFundsTransfer_Inputs";
 
    
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
    public void testEnqCustomerDetsScvs() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(enqCustomerDetsScvs).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
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
    public void testEnqPctCustomerAccounts() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(enqPctCustomerAccounts).get();
        
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
    public void testVerFundsTransfer_Inputs() {
        InteractionSession session = DefaultInteractionSession.newSession();
        session.basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD)
                .url()
                .baseuri(Configuration.DATA_SERVICE_URL)
                .path(verFundsTransfer_Inputs).get();
        
        assertTrue(session.entities().isCollection());
        assertFalse(session.entities().collection().isEmpty());
    }
}