package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.utils.HibernateDataGenerator;
import io.github.bonigarcia.wdm.ChromeDriverManager;

public class LogEntryStepDefs {
    private WebDriver    driver;
    private final String baseUrl = "http://localhost:8080/iTrust2";
    WebDriverWait        wait;

    @Before
    public void setup () {
        ChromeDriverManager.getInstance().setup();
        final ChromeOptions options = new ChromeOptions();
        options.addArguments( "headless" );
        options.addArguments( "window-size=1200x600" );
        options.addArguments( "blink-settings=imagesEnabled=false" );
        driver = new ChromeDriver( options );
        wait = new WebDriverWait( driver, 5 );
        HibernateDataGenerator.refreshDB(); // hcp should have valid email
    }

    @After
    public void tearDown () {
        driver.close();
        driver.quit();
    }

    @Given ( "A patient already exists in the system" )
    public void patientExists () {
        // All tests can safely assume the existence of the 'hcp', 'admin', and
        // 'patient' users
    }

    @When ( "I Log in as an existing patient" )
    public void loginAsPatient () {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "patient" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

    }

    @Then ( "The home page has 1 log entries" )
    public void patientupdatedSuccessfully () {
        final List<WebElement> cells = driver.findElements( By.tagName( "tr" ) );
        // there should be 1 row for header
        assertEquals( cells.size(), 2 );

        // logout
        driver.findElement( By.id( "logout" ) ).click();

    }

    @Given ( "A admin already exists in the system" )
    public void adminExists () {
        // All tests can safely assume the existence of the 'hcp', 'admin', and
        // 'patient' users
    }

    @When ( "I Log in as an existing admin" )
    public void loginAsAdmin () {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "admin" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

    }

    @Given ( "A hcp already exists in the system" )
    public void hcpExists () {
        // All tests can safely assume the existence of the 'hcp', 'admin', and
        // 'patient' users
    }

    @When ( "I Log in as an existing hcp" )
    public void loginAsHcp () {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "hcp" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

    }

    @Given ( "Previous patient still exists in the system" )
    public void patientPreviousExists () {
        // All tests can safely assume the existence of the 'hcp', 'admin', and
        // 'patient' users
    }

    @When ( "I make 10 appointment and go to home page" )
    public void make11Appointments () {
        for ( int i = 0; i <= 10; i++ ) {
            ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('requestappointment').click();" );
            final WebElement date = driver.findElement( By.id( "date" ) );
            date.clear();
            final SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy", Locale.ENGLISH );
            final Long value = Calendar.getInstance().getTimeInMillis()
                    + 1000 * 60 * 60 * 24 * 14; /* Two weeks */
            final Calendar future = Calendar.getInstance();
            future.setTimeInMillis( value );
            date.sendKeys( sdf.format( future.getTime() ) );
            final WebElement time = driver.findElement( By.id( "time" ) );
            time.clear();
            time.sendKeys( "11:59 PM" );
            final WebElement comments = driver.findElement( By.id( "comments" ) );
            comments.clear();
            comments.sendKeys( "Test appointment please ignore" );
            driver.findElement( By.className( "btn" ) ).click();
        }

        driver.findElement( By.className( "navbar-brand" ) ).click();
    }

    @Given ( "Previous admin still exists in the system" )
    public void adminPreviousExists () {
        // All tests can safely assume the existence of the 'hcp', 'admin', and
        // 'patient' users
    }

    @When ( "I add 10 hospitals and go to home page" )
    public void add11Hospitals () {
        // add 11 hospitals
        for ( int i = 0; i < 11; i++ ) {
            final String hospitalName = "TimHortons" + ( new Random() ).nextInt();
            ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('addhospital').click();" );
            final WebElement name = driver.findElement( By.id( "name" ) );
            name.clear();
            name.sendKeys( hospitalName );

            final WebElement address = driver.findElement( By.id( "address" ) );
            address.clear();
            address.sendKeys( "121 Canada Road" );

            final WebElement state = driver.findElement( By.id( "state" ) );
            final Select dropdown = new Select( state );
            dropdown.selectByVisibleText( "CA" );

            final WebElement zip = driver.findElement( By.id( "zip" ) );
            zip.clear();
            zip.sendKeys( "00912" );

            driver.findElement( By.className( "btn" ) ).click();
        }
        driver.findElement( By.className( "navbar-brand" ) ).click();
    }

    @Then ( "The home page has 10 log entries" )
    public void patient10Logs () {
        final List<WebElement> cells = driver.findElements( By.tagName( "tr" ) );
        // there should be 1 row for header
        assertEquals( cells.size(), 11 );

    }

}
