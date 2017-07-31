package fish.payara.tests.external.cargotracker.integration;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import org.openqa.selenium.*;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author Fraser Savage This test class is used to automate testing that books a new cargo journey, views the details
 * and itinerary and changes the destination.
 */
@RunWith(Arquillian.class)
public class BookingTestRemix {

    private static final Logger log = Logger.getLogger(BookingTestRemix.class.getCanonicalName());
    private static String newCargoId;
    private HtmlUnitDriver driver;
    private WebDriverWait wait;

    @ArquillianResource
    private URL deploymentUrl;

    @Rule
    public TestName testName = new TestName();

    /**
     * Deploys the war to the application server.
     *
     * @return
     */
    @Deployment
    public static WebArchive createDeployment() {
        WebArchive war = ShrinkWrap.create(MavenImporter.class)
                .loadPomFromFile("pom.xml").importBuildOutput().as(WebArchive.class);
        return war;
    }

    @Before
    @RunAsClient
    public void setUp() {
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        driver = new HtmlUnitDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.setJavascriptEnabled(true);
        wait = new WebDriverWait(driver, 30);
    }

    @Test
    @RunAsClient
    @InSequence(1)
    public void testOpenAdminInterface() {
        driver.navigate().to(deploymentUrl);
        Assert.assertEquals("Incorrect page title; ", "Cargo Tracker", driver.getTitle());
    }

    @Test
    @RunAsClient
    @InSequence(2)
    public void testBookNewCargo() {
        // Landing Page
        System.out.println("At landing page");
        driver.navigate().to(deploymentUrl);
        assertEquals("Cargo Tracker", driver.getTitle());
        driver.findElement(By.id("adminLandingLink")).click();

        // Go to Booking
        System.out.println("At booking page");
        assertEquals("Cargo Dashboard", driver.getTitle());
        wait.until(ExpectedConditions.elementToBeClickable(By.id("j_idt15:book")));
        driver.findElement(By.id("j_idt15:book")).click();

        // Set Origin
        System.out.println("Setting origin");
        assertEquals("Cargo Registration", driver.getTitle());
        wait.until(ExpectedConditions.elementToBeClickable(By.id("j_idt24:j_idt31")));
        Select origin = new Select(driver.findElement(By.name("j_idt24:origin_input")));
        origin.selectByValue("USCHI");
        driver.findElement(By.id("j_idt24:j_idt31")).click();

        // Set Destination
        System.out.println("Setting destination");
        assertEquals("Cargo Registration", driver.getTitle());
        wait.until(ExpectedConditions.elementToBeClickable(By.id("j_idt24:j_idt32")));
        origin = new Select(driver.findElement(By.name("j_idt24:destination_input")));
        origin.selectByValue("JNTKO");
        driver.findElement(By.id("j_idt24:j_idt32")).click();

        // Set Deadline
        System.out.println("Setting deadline");
        assertEquals("Cargo Registration", driver.getTitle());
        //  3 months
        for (int i = 0; i < 3; i++) {
            wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Next")));
            driver.findElement(By.linkText("Next")).click();
        }
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("18")));
        driver.findElement(By.linkText("18")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("dateForm:bookBtn")));
        driver.findElement(By.id("dateForm:bookBtn")).click();
        
        // Go to routing option from table
        System.out.println("Setting routing");
        assertEquals("Cargo Dashboard", driver.getTitle());
        newCargoId = driver.findElement(By.id("mainDash:tableNotRouted:1:trackingId")).getText();
        assertEquals("Chicago - USCHI", driver.findElement(By.xpath("//tbody[@id='mainDash:tableNotRouted_data']/tr[2]/td[2]")).getText());
        assertEquals("Tokyo - JNTKO", driver.findElement(By.id("mainDash:tableNotRouted:1:toUpdate")).getText());
        wait.until(ExpectedConditions.elementToBeClickable(By.id("mainDash:tableNotRouted:1:trackingId")));
        driver.findElement(By.id("mainDash:tableNotRouted:1:trackingId")).click();
//        System.out.println(driver.getCurrentUrl());
        // Select routing option
//        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("title")));
//        wait.until(ExpectedConditions.titleContains(newCargoId));
        //  assertEquals("Route cargo " + newCargoId, driver.getTitle());
//        try {
//            Thread.sleep(3000);
////        driver.navigate().to(deploymentUrl);
////        driver.findElement(By.id("adminLandingLink")).click();
//        } catch (InterruptedException ex) {
//            Logger.getLogger(BookingTestRemix.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        assertNotEquals("Hit error page", "Error - javax.ejb.EJBTransactionRolledbackException", driver.getTitle());

        assertEquals("Set route for cargo " + newCargoId, driver.findElement(By.id("j_idt26")).getText());
        assertEquals("Chicago", driver.findElement(By.id("j_idt31")).getText());
        assertEquals("Tokyo", driver.findElement(By.id("j_idt35")).getText());
        driver.findElement(By.xpath("//a[@id='j_idt48:0:j_idt50']/span")).click();
        assertEquals("Details", driver.getTitle());
        assertEquals("Routing Details of cargo " + newCargoId, driver.findElement(By.id("j_idt26")).getText());
        assertEquals("Chicago", driver.findElement(By.id("j_idt31")).getText());
        assertEquals("Tokyo", driver.findElement(By.id("j_idt36")).getText());
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
    }
//
//    // TODO Create test to track new cargo through public interface.
//    @Test
//    @RunAsClient
//    @InSequence(2)
//    public void testPublicTrackNewCargo() {
//        
//        try {
//            
//            HtmlPage enterCargoIdPage = landingPageResponse.getElementById("publicLandingLink").click();
//            enterCargoIdPage.getElementById("trackingForm:trackingIdInput").setAttribute("value", newCargoId);
//            System.out.println(enterCargoIdPage.getElementById("trackingForm:trackingIdInput").getAttribute("value"));
//            HtmlPage trackingPage = enterCargoIdPage.getElementById("trackingForm:submitTrack").click();
//            Assert.assertTrue("Handling history did not show expected first event.", trackingPage.asText().contains("Cargo "+newCargoId+" is now: Not received"));
//            Assert.assertTrue("Handling history did not show expected second event.", trackingPage.asText().contains("Estimated time of arrival in Tokyo"));
//            //Assert.assertTrue("Handling history did not show expected third event.", trackingPage.asText().contains("Next expected activity is to receive cargo in Chicago"));
//
//        } catch (IOException ex) {
//            Assert.fail("An IOException was thrown during the test for class \"" + BookingTest.class.getSimpleName() + "\" at method \"" + testName.getMethodName() + "\" with message: " + ex.getMessage());
//        }
//    
//    }
//
//    // TODO Create test to track new cargo through admin interface.
//    @Test
//    @RunAsClient
//    @InSequence(3)
//    public void testAdminTrackNewCargo() {
//        
//        try {
//            
//            HtmlPage adminDashboard = landingPageResponse.getElementById("adminLandingLink").click();
//            Assert.assertThat("Page title was not as expected for the admin dashboard. Expected \"Cargo Dashboard\" but actual was \"" + adminDashboard.getTitleText() + "\"." , adminDashboard.getTitleText(), is("Cargo Dashboard"));
//            HtmlPage enterCargoIdPage = adminDashboard.getElementById("adminTracking").click();
//            enterCargoIdPage.getElementById("trackingForm:trackingIdInput").setAttribute("value", newCargoId);
//            System.out.println(enterCargoIdPage.getElementById("trackingForm:trackingIdInput").getAttribute("value"));
//            HtmlPage trackingPage = enterCargoIdPage.getElementById("trackingForm:submitTrack").click();
//            Assert.assertTrue("Handling history did not show expected first event.", trackingPage.asText().contains("Cargo "+newCargoId+" is now: Not received"));
//            Assert.assertTrue("Handling history did not show expected second event.", trackingPage.asText().contains("Estimated time of arrival in Tokyo"));
//            //Assert.assertTrue("Handling history did not show expected third event.", trackingPage.asText().contains("Next expected activity is to receive cargo in Chicago"));
//        } catch (IOException ex) {
//            Assert.fail("An IOException was thrown during the test for class \"" + BookingTest.class.getSimpleName() + "\" at method \"" + testName.getMethodName() + "\" with message: " + ex.getMessage());        }
//    
//    
//    }
//
//    // TODO Create test to view details of new cargo through the admin interface.
//    @Test
//    @RunAsClient
//    @InSequence(4)
//    public void testViewDetailsNewCargo() {
//        
//        try {
//            
//            //Stores the adminDashboard as a HtmlPage object.
//            HtmlPage adminDashboard = landingPageResponse.getElementById("adminLandingLink").click();
//            Assert.assertThat("Page title was not as expected for the admin dashboard. Expected \"Cargo Dashboard\" but actual was \"" + adminDashboard.getTitleText() + "\"." , adminDashboard.getTitleText(), is("Cargo Dashboard"));
//            //Stores the details page as a HtmlPage object.           
//            HtmlPage detailsPage = adminDashboard.getAnchorByText(newCargoId).click();
//            
//
//            Assert.assertTrue("Origin was not as expected", detailsPage.asText().contains("Origin	Chicago (USCHI)"));
//            Assert.assertTrue("Destination was not as expected", detailsPage.asText().contains("Destination	Tokyo (JNTKO)"));
//        } catch (IOException ex) {
//            Assert.fail("An IOException was thrown during the test for class \"" + BookingTest.class.getSimpleName() + "\" at method \"" + testName.getMethodName() + "\" with message: " + ex.getMessage());
//        }
//    }
//
//    // TODO Create test to change the destination of the new cargo through the admin interface.
//    @Test
//    @RunAsClient
//    @InSequence(5)
//    public void testChangeEndNewCargo() {
//        
//        
//        
//        try {
//            
//            HtmlPage adminDashboard = landingPageResponse.getElementById("adminLandingLink").click();
//            Assert.assertThat("Page title was not as expected for the admin dashboard. Expected \"Cargo Dashboard\" but actual was \"" + adminDashboard.getTitleText() + "\"." , adminDashboard.getTitleText(), is("Cargo Dashboard"));
//            HtmlPage detailsPage = adminDashboard.getAnchorByText(newCargoId).click();
//            Assert.assertThat("Page title was not as expected for the admin dashboard. Expected \"Cargo Administration\" but actual was \"" + detailsPage.getTitleText() + "\".", detailsPage.getTitleText(), is("Cargo Administration"));
//            HtmlPage destinationPage = detailsPage.getAnchorByText("Change destination").click();
//        
//            HtmlSelect getDestinations = destinationPage.getElementByName("j_idt14:j_idt16");
//            HtmlOption selectDestination = getDestinations.getOptionByText("Stockholm (SESTO)");
//            getDestinations.setSelectedAttribute(selectDestination, true);
//            HtmlPage confirmationPage = destinationPage.getElementByName("j_idt14:j_idt19").click();
//            Assert.assertTrue("Destination was not as expected", confirmationPage.asText().contains("Destination	Stockholm (SESTO)"));
//        } catch (IOException ex) {
//            Assert.fail("An IOException was thrown during the test for class \"" + BookingTest.class.getSimpleName() + "\" at method \"" + testName.getMethodName() + "\" with message: " + ex.getMessage());
//        }
//        
//    }
}
