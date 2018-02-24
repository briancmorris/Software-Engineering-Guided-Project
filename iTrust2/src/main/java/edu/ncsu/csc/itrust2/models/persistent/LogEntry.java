package edu.ncsu.csc.itrust2.models.persistent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Class that represents a LogEntry that is created in response to certain user
 * actions. Contains a required TransactionType code (specifying the event that
 * happened), a username, and a time when the event occurred. Has support for an
 * optional secondary user and message for further elaboration
 *
 * @author Kai Presler-Marshall
 *
 */
@Entity
@Table ( name = "LogEntries" )
public class LogEntry extends DomainObject<LogEntry> {

    /**
     * Type of event that has been logged
     */
    @NotNull
    private TransactionType logCode;

    /**
     * The primary user for the event that has been logged
     */
    @NotNull
    private String          primaryUser;

    /**
     * The timestamp of when the event occurred
     */
    @NotNull
    private Calendar        time;

    /**
     * The secondary user for the event that has been logged (optional)
     */
    private String          secondaryUser;

    /**
     * An additional elaborative message for the event that has been logged.
     * Optional.
     */
    private String          message;

    /**
     * ID of the LogEntry
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long            id;

    /**
     * Retrieve all LogEntries from the database.
     *
     * @return All LogEntries in the system
     */
    @SuppressWarnings ( "unchecked" )
    public static List<LogEntry> getLogEntries () {
        return (List<LogEntry>) getAll( LogEntry.class );
    }

    /**
     * Retrieves a LogEntry from the database or memory cache based on its ID
     * (primary key)
     *
     * @param id
     *            The numeric ID of the LogEntry to find
     * @return Matching LogEntry, or null if nothing was found
     */
    public static LogEntry getById ( final Long id ) {
        try {
            return getWhere( createCriterionAsList( ID, id ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }

    }

    /**
     * Retrieve all LogEntries based on the where clause provided.
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return All matching log entries for the clause provided.
     */
    @SuppressWarnings ( "unchecked" )
    private static List<LogEntry> getWhere ( final List<Criterion> where ) {
        return (List<LogEntry>) getWhere( LogEntry.class, where );
    }

    /**
     * Retrieve all LogEntries where the user provided was either the primary or
     * secondary user on the LogEntry.
     *
     * @param user
     *            The user to match on
     * @return All matching LogEntries
     */
    public static List<LogEntry> getAllForUser ( final String user ) {
        return getWhere( createCriterionList(
                Restrictions.or( createCriterion( "primaryUser", user ), createCriterion( "secondaryUser", user ) ) ) );
    }

    /**
     * Retrieve all the LogEntries for the currently logged in User by the
     * specified date range.
     *
     * @param user
     *            The user to retrieve logs for.
     * @param startDate
     *            Starting date in the date range.
     * @param endDate
     *            Ending date in the date range.
     * @return List of LogEntries sorted by date.
     */
    public static List<LogEntry> getAllByDates ( final String user, final String startDate, final String endDate ) {
        // Parse the start string for year, month, and day.
        final String[] startDateArray = startDate.split( "-" );
        final int startYear = Integer.parseInt( startDateArray[0] );
        final int startMonth = Integer.parseInt( startDateArray[1] );
        final int startDay = Integer.parseInt( startDateArray[2] );

        // Parse the end string for year, month, and day.
        final String[] endDateArray = endDate.split( "-" );
        final int endYear = Integer.parseInt( endDateArray[0] );
        final int endMonth = Integer.parseInt( endDateArray[1] );
        final int endDay = Integer.parseInt( endDateArray[2] );

        // Get calendar instances for start and end dates.
        final Calendar start = Calendar.getInstance();
        start.clear();
        final Calendar end = Calendar.getInstance();
        end.clear();

        // Set their values to the corresponding start and end date.
        start.set( startYear, startMonth, startDay );
        end.set( endYear, endMonth, endDay );

        // Check if the start date happens after the end date.
        if ( start.compareTo( end ) > 0 ) {
            System.out.println( "Start is after End." );
            // Start is after end, return empty list.
            return new ArrayList<LogEntry>();
        }

        // Add 1 day to the end date. EXCLUSIVE boundary.
        end.add( Calendar.DATE, 1 );

        // Get all the log entries for the currently logged in users.
        final List<LogEntry> all = LoggerUtil.getAllForUser( user );
        // Create a new list to return.
        final List<LogEntry> dateEntries = new ArrayList<LogEntry>();

        // Compare the dates of the entries and the given function parameters.
        for ( int i = 0; i < all.size(); i++ ) {
            // The current log entry being looked at in the all list.
            final LogEntry e = all.get( i );
            // Log entry's Calendar object.
            final Calendar eTime = e.getTime();
            // If eTime is after (or equal to) the start date and before the end
            // date, add it to the return list.
            if ( eTime.compareTo( start ) >= 0 && eTime.compareTo( end ) < 0 ) {
                dateEntries.add( e );
            }
        }
        // Return the list.
        return dateEntries;
    }

    /**
     * Create a LogEntry from the most complete set of information.
     *
     * @param code
     *            The type of event that occurred and will be logged.
     * @param primaryUser
     *            The primary user that triggered the event
     * @param secondaryUser
     *            The secondary user involved
     * @param message
     *            An optional message for the event
     */
    public LogEntry ( final TransactionType code, final String primaryUser, final String secondaryUser,
            final String message ) {
        this.setLogCode( code );
        this.setPrimaryUser( primaryUser );
        this.setSecondaryUser( secondaryUser );
        this.setMessage( message );
        this.setTime( Calendar.getInstance() );
    }

    /**
     * Creates an empty LogEntry. Used by Hibernate.
     */
    public LogEntry () {
    }

    /**
     * Retrieves the ID of the LogEntry
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Sets the ID on the LogEntry. Used by Hibernate.
     *
     * @param id
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Retrieves the time when the LogEntry occurred.
     *
     * @return Time
     */
    public Calendar getTime () {
        return this.time;
    }

    /**
     * Retrieves the secondary (optional) user on the LogEntry
     *
     * @return Username of the secondary user
     */
    public String getSecondaryUser () {
        return secondaryUser;
    }

    /**
     * Sets the SecondaryUser on the Log Entry
     *
     * @param secondaryUser
     *            Optional secondary user for the LogEntry
     */
    public void setSecondaryUser ( final String secondaryUser ) {
        this.secondaryUser = secondaryUser;
    }

    /**
     * Retrieves the optional Message on the LogEntry
     *
     * @return Any message present
     */
    public String getMessage () {
        return message;
    }

    /**
     * Sets the optional Message on the LogEntry
     *
     * @param message
     *            New Message to set
     */
    public void setMessage ( final String message ) {
        this.message = message;
    }

    /**
     * Retrieves the type of the LogEntry
     *
     * @return The type
     */
    public TransactionType getLogCode () {
        return logCode;
    }

    /**
     * Sets the Type of the LogEntry
     *
     * @param logCode
     *            New Type of the LogEntry
     */
    public void setLogCode ( final TransactionType logCode ) {
        this.logCode = logCode;
    }

    /**
     * Retrieves the primary User of the LogEntry
     *
     * @return The primary user
     */
    public String getPrimaryUser () {
        return primaryUser;
    }

    /**
     * Sets the primary User of the LogEntry
     *
     * @param primaryUser
     *            The primary user to set.
     */
    public void setPrimaryUser ( final String primaryUser ) {
        this.primaryUser = primaryUser;
    }

    /**
     * Sets the time at which the LogEntry occurred.
     *
     * @param time
     *            Timestamp when the event occurred.
     */
    public void setTime ( final Calendar time ) {
        this.time = time;
    }
}
