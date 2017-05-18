package com.temenos.marketplace;


/**
 * GET integration Flow unirest client library
 * 
 * @author mjangid
 *
 */
public class TestInegrationFlowITCase {
    private static String resourceName = "enqIntegrationFlowCatalogs()";

    /*
     * This test will GET the published flow from T24 whose Id contains 'MarketPlace'
     * */
//    @Test
//    public void testIntegrationFlow() throws UnirestException, IOException {
//        String resourceFilter = "?$filter=substringof('MarketPlace', Id)";
//        String url = Configuration.DATA_SERVICE_URL + resourceName + resourceFilter;
//        Unirest.setDefaultHeader("Accept", "application/atom+xml");
//        HttpResponse<InputStream> response = Unirest.get(url)
//                .basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD).asBinary();
//        System.out.println(IOUtils.toString(response.getBody()));
//    }
}
