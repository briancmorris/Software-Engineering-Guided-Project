package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class LogEntryStepDefs {
    private final WebDriver driver  = new HtmlUnitDriver( true );
    private final String    baseUrl = "http://localhost:8080/iTrust2";

    @Given ( "A patient already exists in the system" )
    public void patientExists () {
        // All tests can safely assume the existence of the 'hcp', 'admin', and
        // 'patient' users
    }

    @When ( "I Log in as an existing patient" )
    public void loginAsPatient () {
        driver.get( baseUrl );
        for ( int i = 5; i <= 0; i++ ) {
            logInAndOut();
        }

    }

    public void logInAndOut () {
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "patient" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

        final WebElement logout = driver.findElement( By.id( "logout" ) );
        logout.click();
    }

    @Then ( "The home page has log entries" )
    public void updatedSuccessfully () {
        final List<WebElement> cells = driver.findElements( By.tagName( "tr" ) );
        assertEquals( cells.size(), 10 );
    }
}
