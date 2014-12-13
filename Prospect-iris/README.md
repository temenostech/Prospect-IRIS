*-iris project
==============

Package the IRIS web application.

## Run integration tests
Note: By default these tests start an embedded jetty server and connect to a TAFC instance that has been preconfigured with our Version/Enquiries from the models project.

$ mvn clean verify -DskipITs=false



## Run integration tests on JBoss

### Build and deploy to your local dev instance

$ mvn -Djboss.deployDir=$YOURMODELBANK_ROOT\Temenos\jboss\standalone\deployments clean install upload:upload -Pdevelopment


### Execute integration tests on JBoss

$ mvn -DframeworkPackaging=tafc -DskipITs=false -DTEST_ENDPOINT_URI=http://127.0.0.1:9081/test-iris/Test.svc/GB0010001/ -DTEST_USERNAME=SSOUSER1 -DTEST_PASSWORD=123456 -DTEST_AUTHORISER_USERNAME=SSOUSER1 -DTEST_AUTHORISER_PASSWORD=123456 -DTEST_T24_DATE=20130322 clean verify

Test with your server somewhere else:

$ mvn -DframeworkPackaging=tafc -DskipITs=false -DTEST_ENDPOINT_URI=http://10.44.5.190:8090/test-iris/Test.svc/GB0010001/ -DTEST_USERNAME=SSOUSER1 -DTEST_PASSWORD=test123 -DTEST_AUTHORISER_USERNAME=SSOUSER1 -DTEST_AUTHORISER_PASSWORD=123456 -DTEST_T24_DATE=20130322 clean verify

