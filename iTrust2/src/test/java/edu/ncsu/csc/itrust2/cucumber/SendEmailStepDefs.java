package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.utils.HibernateDataGenerator;
import io.github.bonigarcia.wdm.ChromeDriverManager;

public class SendEmailStepDefs {
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

    private void setTextField ( final By byval, final Object value ) {
        final WebElement elem = driver.findElement( byval );
        elem.clear();
        elem.sendKeys( value.toString() );
    }

    @Given ( "I login as (.+) with password (.+)" )
    public void login ( final String username, final String password ) {
        driver.get( baseUrl );
        setTextField( By.name( "username" ), username );
        setTextField( By.name( "password" ), password );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

        wait.until( ExpectedConditions.not( ExpectedConditions.titleIs( "iTrust2 :: Login" ) ) );
    }

    @Then ( "I select change password" )
    public void navChangePassword () {
        driver.get( baseUrl + "/changePassword" );
    }

    @And ( "I enter my password (.+) and my (.+) password twice" )
    public void fillChangeForm ( final String password, final String newPassword ) {
        // Wait until page loads
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "currentPW" ) ) );

        setTextField( By.name( "currentPW" ), password );
        setTextField( By.name( "newPW" ), newPassword );
        setTextField( By.name( "confirmPW" ), newPassword );

        final WebElement submit = driver.findElement( By.name( "submitButton" ) );
        submit.click();
    }

    @Then ( "I update my password successfully (.+)" )
    public void checkPwdSuccess ( final String user ) {
        try {
            TimeUnit.SECONDS.sleep( 15 );
            wait.until( ExpectedConditions.textToBePresentInElementLocated( By.name( "message" ),
                    "Password changed successfully" ) );
        }
        catch ( final Exception e ) {
            fail( driver.findElement( By.name( "message" ) ).getText() );
        }
    }

    @And ( "an email is sent to (.+)" )
    public void checkEmailSuccess ( final String user ) {
        driver.get( baseUrl + "/api/v1/logentries" );
        driver.getPageSource().contains( "{\"logCode\":\"EMAIL_PASSWORD_CHANGE\",\"primaryUser\":\"" + user + "\" " );
    }

    @And ( "no email is sent to (.+)" )
    public void checkEmailFail ( final String user ) {
        driver.get( baseUrl + "/api/v1/logentries" );
        driver.getPageSource().contains( "{\"logCode\":\"EMAIL_NOT_SENT\",\"primaryUser\":\"" + user + "\" " );
    }

    @Given ( "I am on the login page" )
    public void loadLogin () {
        // ensures nobody is logged in currently
        driver.get( baseUrl + "/login" );
    }

    @When ( "I select forgot password" )
    public void loadForgotPassword () {
        // navigates to reset page without having to find button from login
        driver.get( baseUrl + "/requestPasswordReset" );
    }

    @Then ( "I enter my username (.+)" )
    public void enterUsername ( final String username ) {
        // Wait until page loads
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "username" ) ) );

        setTextField( By.name( "username" ), username );
        final WebElement submit = driver.findElement( By.name( "submitButton" ) );
        submit.click();
    }

    @Then ( "I reset my password successfully for (.+)" )
    public void resetSuccess ( final String user ) throws InterruptedException {
        TimeUnit.SECONDS.sleep( 15 );
        driver.get( baseUrl + "/api/v1/logentries" );
        driver.getPageSource().contains( "{\"logCode\":\"EMAIL_PASSWORD_CHANGE\",\"primaryUser\":\"" + user + "\" " );
        driver.getPageSource().contains( "{\"logCode\":\"PASSWORD_UPDATE_SUCCESS\",\"primaryUser\":\"" + user + "\" " );
    }

    @Then ( "I do not reset my password successfully for (.+)" )
    public void resetFailure ( final String user ) throws InterruptedException {
        TimeUnit.SECONDS.sleep( 3 );
        driver.get( baseUrl + "/api/v1/logentries" );
        driver.getPageSource().contains( "{\"logCode\":\"EMAIL_NOT_SENT\",\"primaryUser\":\"" + user + "\" " );
        driver.getPageSource().contains( "{\"logCode\":\"PASSWORD_UPDATE_FAILURE\",\"primaryUser\":\"" + user + "\" " );
    }

}
