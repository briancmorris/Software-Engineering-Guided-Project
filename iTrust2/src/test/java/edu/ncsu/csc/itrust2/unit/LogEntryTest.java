package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.LogEntry;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Unit tests for the LoggerUtil and LogEntry classes that reflect the changes
 * made in the guided project.
 *
 * @author Brian Morris
 *
 */
public class LogEntryTest {

    /**
     * Tests the newly added changes made during the guided project, for sorting
     * by date.
     */
    @Test
    public void testLogsByDate () {
        // Create a user to work with.
        final User testUser = new User();
        testUser.setUsername( "brianTestHCP" );
        testUser.setRole( Role.ROLE_HCP );

        // Get a (hopefully) empty list for entries that haven't been created.
        List<LogEntry> testList = LoggerUtil.getAllByDates( "brianTestHCP", "2019-01-01", "2019-01-02" );
        assertEquals( testList.size(), 0 );

        // Get an empty list for an end date that is before start date.
        testList = LoggerUtil.getAllByDates( "brianTestHCP", "2019-01-02", "2019-01-01" );
        assertEquals( testList.size(), 0 );

        // Log an event for now.
        LoggerUtil.log( TransactionType.VIEW_ACCESS_LOGS, "brianTestHCP" );
        final Calendar now = Calendar.getInstance();
        testList = LoggerUtil.getAllByDates( "brianTestHCP",
                "" + now.get( Calendar.YEAR ) + "-" + now.get( Calendar.MONTH ) + "-"
                        + now.get( Calendar.DAY_OF_MONTH ),
                "" + now.get( Calendar.YEAR ) + "-" + now.get( Calendar.MONTH ) + "-"
                        + now.get( Calendar.DAY_OF_MONTH ) );
        assertTrue( testList.size() > 0 );
    }

    /**
     * Tests the newly added changes made during the guided project, for making
     * a table with only 10 entries.
     */
    @Test
    public void testLogsByPatient () {
        // Create a user to work with.
        final User testUser = new User();
        testUser.setUsername( "brianTestPatient" );
        testUser.setRole( Role.ROLE_PATIENT );
        // Test getBottomForPatient.
        LoggerUtil.log( TransactionType.VIEW_ACCESS_LOGS, "brianTestPatient" );
        List<LogEntry> testList = LoggerUtil.getBottomForPatient( "brianTestPatient" );
        assertTrue( testList.size() > 0 );

        // Log 10 additional events to ensure the event log is > 10.
        LoggerUtil.log( TransactionType.VIEW_ACCESS_LOGS, "brianTestPatient" );
        LoggerUtil.log( TransactionType.VIEW_ACCESS_LOGS, "brianTestPatient" );
        LoggerUtil.log( TransactionType.VIEW_ACCESS_LOGS, "brianTestPatient" );
        LoggerUtil.log( TransactionType.VIEW_ACCESS_LOGS, "brianTestPatient" );
        LoggerUtil.log( TransactionType.VIEW_ACCESS_LOGS, "brianTestPatient" );
        LoggerUtil.log( TransactionType.VIEW_ACCESS_LOGS, "brianTestPatient" );
        LoggerUtil.log( TransactionType.VIEW_ACCESS_LOGS, "brianTestPatient" );
        LoggerUtil.log( TransactionType.VIEW_ACCESS_LOGS, "brianTestPatient" );
        LoggerUtil.log( TransactionType.VIEW_ACCESS_LOGS, "brianTestPatient" );
        LoggerUtil.log( TransactionType.VIEW_ACCESS_LOGS, "brianTestPatient" );

        // Check that getBottomForPatient returns only 10 entries.
        testList = LoggerUtil.getBottomForPatient( "brianTestPatient" );
        assertEquals( testList.size(), 10 );

        // Check that getBottomforUser returns only 10 entries.
        testList = LoggerUtil.getBottomForUser( "brianTestPatient", new Integer( 10 ) );
        assertEquals( testList.size(), 10 );
    }

    /**
     * Tests the LoggerUtilFunctionality with User objects.
     */
    @Test
    public void testLogsByUser () {
        // Create a user to work with.
        final User testUser1 = new User();
        testUser1.setUsername( "brianTestUser1" );
        testUser1.setRole( Role.ROLE_ADMIN );

        // Log using a User object instead of user name.
        LoggerUtil.log( TransactionType.VIEW_ACCESS_LOGS, testUser1 );
        final List<LogEntry> testList1 = LoggerUtil.getBottomForUser( "brianTestUser1", new Integer( 10 ) );
        assertTrue( testList1.size() > 0 );

        // Create two users to work with.
        final User testUser2 = new User();
        testUser2.setUsername( "brianTestUser2" );
        testUser2.setRole( Role.ROLE_HCP );
        final User testUser3 = new User();
        testUser3.setUsername( "brianTestUser3" );
        testUser3.setRole( Role.ROLE_PATIENT );

        // Log using User objects instead of user names.
        LoggerUtil.log( TransactionType.APPOINTMENT_REQUEST_APPROVED, testUser2, testUser3 );
        // Get the lists for each user.
        final List<LogEntry> testList2 = LoggerUtil.getBottomForUser( "brianTestUser2", new Integer( 10 ) );
        final List<LogEntry> testList3 = LoggerUtil.getBottomForPatient( "brianTestUser3" );

        assertEquals( testList2.size(), testList3.size() );

        final String current = LoggerUtil.currentUser();
        assertTrue( current.equals( "SPRING_API_TEST_USER" ) );

        final User testUser4 = new User();
        testUser4.setUsername( "brianTestUser4" );
        testUser4.setRole( Role.ROLE_ADMIN );

        LoggerUtil.log( TransactionType.VIEW_ACCESS_LOGS, testUser4 );
        final List<LogEntry> testList4 = LoggerUtil.getTopForUser( "brianTestUser4", new Integer( 10 ) );
        assertTrue( testList4.size() > 0 );

    }
}
