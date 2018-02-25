package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.LogEntry;
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
        // Get a (hopefully) empty list for entries that haven't been created.
        List<LogEntry> testList = LoggerUtil.getAllByDates( "logHcp", "2019-01-01", "2019-01-02" );
        assertEquals( testList.size(), 0 );

        // Get an empty list for an end date that is before start date.
        testList = LoggerUtil.getAllByDates( "logHcp", "2019-01-02", "2019-01-01" );
        assertEquals( testList.size(), 0 );

        // Log an event for now.
        LoggerUtil.log( TransactionType.VIEW_ACCESS_LOGS, "logHcp" );
        final Calendar now = Calendar.getInstance();
        testList = LoggerUtil.getAllByDates( "logHcp",
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
        // Test getBottomForPatient.
        List<LogEntry> testList = LoggerUtil.getBottomForPatient( "logPatient" );
        assertTrue( testList.size() <= 10 );

        // Log 11 additional events to ensure the event log is > 10.
        LoggerUtil.log( TransactionType.VIEW_ACCESS_LOGS, "logPatient" );
        LoggerUtil.log( TransactionType.VIEW_ACCESS_LOGS, "logPatient" );
        LoggerUtil.log( TransactionType.VIEW_ACCESS_LOGS, "logPatient" );
        LoggerUtil.log( TransactionType.VIEW_ACCESS_LOGS, "logPatient" );
        LoggerUtil.log( TransactionType.VIEW_ACCESS_LOGS, "logPatient" );
        LoggerUtil.log( TransactionType.VIEW_ACCESS_LOGS, "logPatient" );
        LoggerUtil.log( TransactionType.VIEW_ACCESS_LOGS, "logPatient" );
        LoggerUtil.log( TransactionType.VIEW_ACCESS_LOGS, "logPatient" );
        LoggerUtil.log( TransactionType.VIEW_ACCESS_LOGS, "logPatient" );
        LoggerUtil.log( TransactionType.VIEW_ACCESS_LOGS, "logPatient" );
        LoggerUtil.log( TransactionType.VIEW_ACCESS_LOGS, "logPatient" );

        // Check that getBottomForPatient returns only 10 entries.
        testList = LoggerUtil.getBottomForPatient( "logPatient" );
        assertEquals( testList.size(), 10 );

        // Check that getBottomforUser returns only 10 entries.
        testList = LoggerUtil.getBottomForUser( "logPatient", new Integer( 10 ) );
        assertEquals( testList.size(), 10 );
    }
}
