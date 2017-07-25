//package fish.payara.tests.external.cargotracker.integration;
//
//import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
//import com.gargoylesoftware.htmlunit.WebClient;
//import com.gargoylesoftware.htmlunit.html.HtmlButton;
//import com.gargoylesoftware.htmlunit.html.HtmlElement;
//import com.gargoylesoftware.htmlunit.html.HtmlOption;
//import com.gargoylesoftware.htmlunit.html.HtmlPage;
//import com.gargoylesoftware.htmlunit.html.HtmlSelect;
//import java.io.IOException;
//import org.jboss.arquillian.container.test.api.Deployment;
//import org.jboss.arquillian.container.test.api.RunAsClient;
//import org.jboss.arquillian.junit.Arquillian;
//import org.jboss.arquillian.junit.InSequence;
//import org.jboss.arquillian.test.api.ArquillianResource;
//import org.jboss.shrinkwrap.api.ShrinkWrap;
//import org.jboss.shrinkwrap.api.spec.WebArchive;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.rules.TestName;
//import org.junit.runner.RunWith;
//
//import java.net.URL;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import static org.hamcrest.CoreMatchers.is;
//import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;
//import org.junit.Assert;
//import org.junit.Before;
//
///**
// * @author Fraser Savage This test class is used to automate testing that books a new cargo journey, views the details
// * and itinerary and changes the destination.
// */
//@RunWith(Arquillian.class)
//public class BookingTest {
//
//    private static final Logger log = Logger.getLogger(BookingTest.class.getCanonicalName());
//
//    private static String newCargoId;
//
//    /**
//     * Deploys the war to the application server.
//     *
//     * @return
//     */
//    @Deployment
//    public static WebArchive createDeployment() {
//        WebArchive war = ShrinkWrap.create(MavenImporter.class)
//                .loadPomFromFile("pom.xml").importBuildOutput().as(WebArchive.class);
//
//        return war;
//    }
//
//    @ArquillianResource
//    private URL deploymentUrl;
//
//    @Rule
//    public TestName testName = new TestName();
//
//    private WebClient browser;
//
//    private HtmlPage landingPageResponse;
//
//    @Before
//    @RunAsClient
//    public void setUp() {
//        try {
//            browser = new WebClient();
//            java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
//            browser.getOptions().setThrowExceptionOnScriptError(false);
//            browser.getOptions().setJavaScriptEnabled(true);
//            landingPageResponse = browser.getPage(deploymentUrl.toString() + "index.xhtml");
//            Assert.assertEquals("Could not load the application landing page.", "Cargo Tracker", landingPageResponse.getTitleText());
//        } catch (IOException | FailingHttpStatusCodeException ex) {
//            Logger.getLogger(BookingTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    @Test
//    @RunAsClient
//    @InSequence(1)
//    public void testOpenAdminInterface() {
//        try {
//            HtmlPage admin = landingPageResponse.getElementById("adminLandingLink").click();
//            Assert.assertThat("Page title not expected" + admin.getTitleText(), admin.getTitleText(),
//                    is("Cargo Dashboard"));
//        } catch (IOException ex) {
//            Logger.getLogger(BookingTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    @Test
//    @RunAsClient
//    @InSequence(2)
//    public void testBookNewCargo() {
//        try {
//            HtmlPage admin = landingPageResponse.getElementById("adminLandingLink").click();
//            // Sleeps are to handle the tests executing faster than the pages load
//            Thread.sleep(1000);
//            Assert.assertEquals("According to the page title, this is the incorrect page.",
//                    "Cargo Dashboard", admin.getTitleText());
//
//            HtmlPage bookCargoOriginPage = admin.getElementById("j_idt15:book").click();
//            Thread.sleep(2000);
//            Assert.assertEquals("According to the page title, this is the incorrect page",
//                    "Cargo Registration", bookCargoOriginPage.getTitleText());
//            HtmlSelect getOrigins = bookCargoOriginPage.getElementByName("j_idt24:origin_input");
//            Assert.assertTrue("Origin list does not appear to contain \"Chicago (USCHI)\".",
//                    getOrigins.getOptionByText("Chicago (USCHI)") != null);
//            HtmlOption selectedOrigin = getOrigins.getOptionByText("Chicago (USCHI)");
//            getOrigins.setSelectedAttribute(selectedOrigin, true);
//
//            HtmlPage bookCargoDestinationPage = bookCargoOriginPage.getElementById("j_idt24:j_idt31").click();
//            Thread.sleep(2000);
//            Assert.assertEquals("According to the page title, this is the incorrect page.",
//                    "Cargo Registration", bookCargoDestinationPage.getTitleText());
//            HtmlElement destinations = bookCargoDestinationPage.getElementByName("j_idt24:destination_input");
//            Assert.assertTrue("The destinations list does not appear to have been rendered.", destinations != null);
//            HtmlSelect getDestinations = bookCargoDestinationPage.getElementByName("j_idt24:destination_input");
//            Assert.assertTrue("Destination list does not appear to contain \"Tokyo (JNTKO)\"",
//                    getDestinations.getOptionByText("Tokyo (JNTKO)") != null);
//            HtmlOption selectedDestination = getDestinations.getOptionByText("Tokyo (JNTKO)");
//            getDestinations.setSelectedAttribute(selectedDestination, true);
//
//            HtmlPage bookCargoDeadlinePage = bookCargoDestinationPage.getElementById("j_idt24:j_idt32").click();
//            Thread.sleep(2000);
//            Assert.assertEquals("According to the page title, this is the incorrect page.",
//                    "Cargo Registration", bookCargoDeadlinePage.getTitleText());
//            // Go three months ahead
//            for (int i = 0; i < 3; i++) {
//                ((HtmlElement) bookCargoDeadlinePage.getFirstByXPath(
//                        "//*[@id=\"dateForm:deadline_inline\"]/div/div/a[2]")).click();
//                Thread.sleep(1000);
//            }
//            ((HtmlElement) bookCargoDeadlinePage.getFirstByXPath(
//                    "//*[@id=\"dateForm:deadline_inline\"]/div/table/tbody/tr[3]/td[4]/a")).click();
//            log.log(Level.INFO, "Cargo deadline set to: \"{0}\".",
//                    bookCargoDeadlinePage.getElementById("dateForm:deadline_input").getAttribute("value"));
//            //     bookCargoDeadlinePage.getElementById("dateForm:bookBtn").click();
//
////            HtmlPage cargoDashboard = bookCargoDeadlinePage.getElementById("j_idt15:dashboard").click();
//            HtmlPage cargoDashboard = bookCargoDeadlinePage.getElementById("dateForm:bookBtn").click();
//            
////            HtmlButton submit = (HtmlButton) bookCargoDeadlinePage.getElementById("dateForm:bookBtn");
////            submit.click();
////            
////            Assert.assertEquals("According to the page title, this is the incorrect page.",
////                    "Cargo Dashboard", cargoDashboard.getTitleText());
////            Assert.assertEquals("The routing tables do not appear present.", "Routed", cargoDashboard.getElementById("mainDash:Routed_header").asText());
////            Assert.assertEquals("The default non-routed entry does not appear present.", "Hong Kong -", cargoDashboard.getFirstByXPath("//*[@id=\"mainDash:tableNotRouted_data\"]/tr/td[2]/text()").toString());
////            Assert.assertEquals("The new entry does not appear present.", "Chicago -", cargoDashboard.getFirstByXPath("//*[@id=\"mainDash:tableNotRouted_data\"]/tr[2]/td[2]/text()").toString());
////            newCargoId = cargoDashboard.getFirstByXPath("//*[@id=\"mainDash:tableNotRouted_data\"]/tr[2]/td[1]/text()").toString();
////            System.out.println(newCargoId);
//            //*[@id="mainDash:tableNotRouted_data"]/tr[2]/td[2]/text()
//            //*[@id="mainDash:tableNotRouted_data"]/tr/td[2]/text()
//            //*[@id="mainDash:tableNotRouted:1:trackingId"]
////            newCargoId = cargoDashboard.getElementById("mainDash:tableNotRouted:0:trackingId").asText();
////            System.out.println(newCargoId);
////            newCargoId = cargoDashboard.getFirstByXPath(
////                    "//*[@id=\"mainDash:tableNotRouted:0:trackingId\"]/text()").toString().trim();
////            //*[@id="mainDash:tableNotRouted:1:trackingId"]
////            System.out.println(newCargoId);
////            
////            HtmlPage cargoRoutingPage = cargoDashboard.getElementById("mainDash:tableNotRouted:1:trackingId").click();
////            Assert.assertEquals("According to the page title, this is the incorrect page.", 
////                    "Route Cargo " + newCargoId, cargoRoutingPage.getTitleText());
////
////            HtmlPage selectedRoute = cargoRoutingPage.getElementById("j_idt48:0:j_idt50").click();
////            Assert.assertEquals("According to the page title, this is the incorrect page.", 
////                    "Details", selectedRoute.getTitleText());
////            Assert.assertEquals("The origin appears incorrect",
////                    "Chicago", selectedRoute.getElementById("j_idt31").asText());
////            Assert.assertEquals("The destination appears incorrect.",
////                    "Tokyo ", selectedRoute.getElementById("j_idt36").asText());
////            Assert.assertEquals("The cargo ID appears to be incorrect.",
////                    "Routing Details of cargo " + newCargoId, selectedRoute.getElementById("i_idt26").asText());
////            
////            log.log(Level.INFO, "Successfully booked new cargo with the ID: \"{0}\".", newCargoId);
//        } catch (InterruptedException | IOException ex) {
//            Logger.getLogger(BookingTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
////    @Test
////    @RunAsClient
////    @InSequence(1)
////    public void testBookNewCargo() {
////        
////        try {
////            //book cargo and set the new cargo ID as the value of newCargoId
////            
////            HtmlPage admin = landingPageResponse.getElementById("adminLandingLink").click();
////            Assert.assertThat("Page title was not as expected for the admin dashboard. Expected \"Cargo Dashboard\" but actual was \"" + admin.getTitleText() + "\"." , admin.getTitleText(), is("Cargo Dashboard"));
////            HtmlPage makeBooking = admin.getElementById("adminBooking").click();
////            Assert.assertThat("Page title was not as expected for the admin dashboard. Expected \"Cargo Administration\" but actual was \"" + makeBooking.getTitleText() + "\"." , makeBooking.getTitleText(), is("Cargo Administration"));
////            HtmlSelect getDestinations = makeBooking.getElementByName("registrationForm:j_idt17");
////            HtmlOption selectDestination = getDestinations.getOptionByText("Tokyo (JNTKO)");
////            getDestinations.setSelectedAttribute(selectDestination, true);
////            HtmlDateInput dateInput = makeBooking.getElementByName("registrationForm:j_idt20");
////            dateInput.setValueAttribute("2016-06-06");
////            HtmlPage confirmationPage = makeBooking.getElementByName("registrationForm:j_idt22").click();
////            Assert.assertTrue("", confirmationPage.asText().contains("Chicago (USCHI)") );
////            Assert.assertTrue("", confirmationPage.asText().contains("Tokyo (JNTKO)") );
////            List<?> getID = confirmationPage.getByXPath("//span[@class='success label']/text()");
////            Object cargoIDPhrase = getID.get(0);
////            String [] fragments = cargoIDPhrase.toString().split(" ");
////            newCargoId = fragments[3];
////            
////            //route cargo + routing breaks as shrinkwrap renames the war with _DEFAULT__DEFAULT
////            HtmlAnchor anchor = confirmationPage.getAnchorByHref("/cargo-tracker/admin/selectItinerary.xhtml?trackingId="+newCargoId);
////            HtmlPage router = anchor.click();
////           // HtmlPage route = router.getElementByName("j_idt16:0:j_idt17:j_idt22").click();
////        }
////        catch(IOException ie) {
////            Assert.fail("An IOException was thrown during the test for class \"" + BookingTest.class.getSimpleName() + "\" at method \"" + testName.getMethodName() + "\" with message: " + ie.getMessage());
////
////        }
////            
////        log.log(Level.INFO, "Successfully booked new cargo with Id \"" + newCargoId + "\".");
////    }
////
////    // TODO Create test to track new cargo through public interface.
////    @Test
////    @RunAsClient
////    @InSequence(2)
////    public void testPublicTrackNewCargo() {
////        
////        try {
////            
////            HtmlPage enterCargoIdPage = landingPageResponse.getElementById("publicLandingLink").click();
////            enterCargoIdPage.getElementById("trackingForm:trackingIdInput").setAttribute("value", newCargoId);
////            System.out.println(enterCargoIdPage.getElementById("trackingForm:trackingIdInput").getAttribute("value"));
////            HtmlPage trackingPage = enterCargoIdPage.getElementById("trackingForm:submitTrack").click();
////            Assert.assertTrue("Handling history did not show expected first event.", trackingPage.asText().contains("Cargo "+newCargoId+" is now: Not received"));
////            Assert.assertTrue("Handling history did not show expected second event.", trackingPage.asText().contains("Estimated time of arrival in Tokyo"));
////            //Assert.assertTrue("Handling history did not show expected third event.", trackingPage.asText().contains("Next expected activity is to receive cargo in Chicago"));
////
////        } catch (IOException ex) {
////            Assert.fail("An IOException was thrown during the test for class \"" + BookingTest.class.getSimpleName() + "\" at method \"" + testName.getMethodName() + "\" with message: " + ex.getMessage());
////        }
////    
////    }
////
////    // TODO Create test to track new cargo through admin interface.
////    @Test
////    @RunAsClient
////    @InSequence(3)
////    public void testAdminTrackNewCargo() {
////        
////        try {
////            
////            HtmlPage adminDashboard = landingPageResponse.getElementById("adminLandingLink").click();
////            Assert.assertThat("Page title was not as expected for the admin dashboard. Expected \"Cargo Dashboard\" but actual was \"" + adminDashboard.getTitleText() + "\"." , adminDashboard.getTitleText(), is("Cargo Dashboard"));
////            HtmlPage enterCargoIdPage = adminDashboard.getElementById("adminTracking").click();
////            enterCargoIdPage.getElementById("trackingForm:trackingIdInput").setAttribute("value", newCargoId);
////            System.out.println(enterCargoIdPage.getElementById("trackingForm:trackingIdInput").getAttribute("value"));
////            HtmlPage trackingPage = enterCargoIdPage.getElementById("trackingForm:submitTrack").click();
////            Assert.assertTrue("Handling history did not show expected first event.", trackingPage.asText().contains("Cargo "+newCargoId+" is now: Not received"));
////            Assert.assertTrue("Handling history did not show expected second event.", trackingPage.asText().contains("Estimated time of arrival in Tokyo"));
////            //Assert.assertTrue("Handling history did not show expected third event.", trackingPage.asText().contains("Next expected activity is to receive cargo in Chicago"));
////        } catch (IOException ex) {
////            Assert.fail("An IOException was thrown during the test for class \"" + BookingTest.class.getSimpleName() + "\" at method \"" + testName.getMethodName() + "\" with message: " + ex.getMessage());        }
////    
////    
////    }
////
////    // TODO Create test to view details of new cargo through the admin interface.
////    @Test
////    @RunAsClient
////    @InSequence(4)
////    public void testViewDetailsNewCargo() {
////        
////        try {
////            
////            //Stores the adminDashboard as a HtmlPage object.
////            HtmlPage adminDashboard = landingPageResponse.getElementById("adminLandingLink").click();
////            Assert.assertThat("Page title was not as expected for the admin dashboard. Expected \"Cargo Dashboard\" but actual was \"" + adminDashboard.getTitleText() + "\"." , adminDashboard.getTitleText(), is("Cargo Dashboard"));
////            //Stores the details page as a HtmlPage object.           
////            HtmlPage detailsPage = adminDashboard.getAnchorByText(newCargoId).click();
////            
////
////            Assert.assertTrue("Origin was not as expected", detailsPage.asText().contains("Origin	Chicago (USCHI)"));
////            Assert.assertTrue("Destination was not as expected", detailsPage.asText().contains("Destination	Tokyo (JNTKO)"));
////        } catch (IOException ex) {
////            Assert.fail("An IOException was thrown during the test for class \"" + BookingTest.class.getSimpleName() + "\" at method \"" + testName.getMethodName() + "\" with message: " + ex.getMessage());
////        }
////    }
////
////    // TODO Create test to change the destination of the new cargo through the admin interface.
////    @Test
////    @RunAsClient
////    @InSequence(5)
////    public void testChangeEndNewCargo() {
////        
////        
////        
////        try {
////            
////            HtmlPage adminDashboard = landingPageResponse.getElementById("adminLandingLink").click();
////            Assert.assertThat("Page title was not as expected for the admin dashboard. Expected \"Cargo Dashboard\" but actual was \"" + adminDashboard.getTitleText() + "\"." , adminDashboard.getTitleText(), is("Cargo Dashboard"));
////            HtmlPage detailsPage = adminDashboard.getAnchorByText(newCargoId).click();
////            Assert.assertThat("Page title was not as expected for the admin dashboard. Expected \"Cargo Administration\" but actual was \"" + detailsPage.getTitleText() + "\".", detailsPage.getTitleText(), is("Cargo Administration"));
////            HtmlPage destinationPage = detailsPage.getAnchorByText("Change destination").click();
////        
////            HtmlSelect getDestinations = destinationPage.getElementByName("j_idt14:j_idt16");
////            HtmlOption selectDestination = getDestinations.getOptionByText("Stockholm (SESTO)");
////            getDestinations.setSelectedAttribute(selectDestination, true);
////            HtmlPage confirmationPage = destinationPage.getElementByName("j_idt14:j_idt19").click();
////            Assert.assertTrue("Destination was not as expected", confirmationPage.asText().contains("Destination	Stockholm (SESTO)"));
////        } catch (IOException ex) {
////            Assert.fail("An IOException was thrown during the test for class \"" + BookingTest.class.getSimpleName() + "\" at method \"" + testName.getMethodName() + "\" with message: " + ex.getMessage());
////        }
////        
////    }
//}
