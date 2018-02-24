package edu.ncsu.csc.itrust2.controllers.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.LogEntry;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * REST controller for interacting with Log Entry-related endpoints This will
 * have somewhat reduced functionality compared to the other controllers because
 * we don't want users to be able to delete logged events (_even_ if they are
 * Personnel/an admin)
 *
 * @author Kai Presler-Marshall
 *
 */
@RestController
@SuppressWarnings ( { "unchecked", "rawtypes" } )
public class APILogEntryController extends APIController {

    /**
     * Retrieves and returns a List of all LogEntries in the system
     *
     * @return list of log entries
     */
    @GetMapping ( BASE_PATH + "/logentries" )
    public List<LogEntry> getLogEntries () {
        return LogEntry.getLogEntries();
    }

    /**
     * Retrieves and returns a specific log entry specified by the id provided.
     *
     * @param id
     *            The id of the log entry, as generated by Hibernate and used as
     *            the primary key
     * @return response
     */
    @GetMapping ( BASE_PATH + "/logentries/{id}" )
    public ResponseEntity getEntry ( @PathVariable ( "id" ) final Long id ) {
        final LogEntry entry = LogEntry.getById( id );
        return null == entry
                ? new ResponseEntity( errorResponse( "No log entry found for id " + id ), HttpStatus.NOT_FOUND )
                : new ResponseEntity( entry, HttpStatus.OK );
    }

    /**
     * Retrieves and returns the top 10 log entries for the currently logged in
     * user.
     *
     * @return response
     */
    @GetMapping ( BASE_PATH + "/logentries/user10" )
    public List<LogEntry> getTopTenLogEntriesForUser () {
        final String user = LoggerUtil.currentUser();
        final List<LogEntry> entries = LoggerUtil.getBottomForUser( user, new Integer( 10 ) );
        return entries;
    }

    /**
     * Retrieves and returns the top 10 log entries for the currently logged in
     * user.
     *
     * @return response
     */
    @GetMapping ( BASE_PATH + "/logentries/patient10" )
    public List<LogEntry> getTopTenPatientLogEntriesForUser () {
        final String user = LoggerUtil.currentUser();
        final List<LogEntry> entries = LoggerUtil.getBottomForPatient( user );
        for ( int i = 0; i < entries.size(); i++ ) {
            final String uName = entries.get( i ).getSecondaryUser();
            entries.get( i ).setMessage( "" );
            if ( uName != null ) {
                final User u = User.getByName( uName );
                final String role = u.getRole().toString();
                entries.get( i ).setMessage( role );
            }
        }

        return entries;
    }

    /**
     * Retrieves and returns the log entries for the currently logged in user.
     *
     * @return response
     */
    @GetMapping ( BASE_PATH + "/logentries/userAll" )
    public List<LogEntry> getAllLogEntriesForUser () {
        final String user = LoggerUtil.currentUser();
        final List<LogEntry> entries = LoggerUtil.getAllForUser( user );
        LoggerUtil.log( TransactionType.VIEW_ACCESS_LOGS, user );
        return entries;
    }

    /**
     * Retrieves and returns the log entries for the currently logged in user
     * for a start and end time
     *
     * @param startDate
     *            start date of the log entries
     * @param endDate
     *            end date of the log entries
     * @return log entries for a time period
     */
    @GetMapping ( BASE_PATH + "/logentries/{startDate}/{endDate}" )
    public List<LogEntry> getDateLogEntries ( @PathVariable ( "startDate" ) final String startDate,
            @PathVariable ( "endDate" ) final String endDate ) {
        final String user = LoggerUtil.currentUser();
        final List<LogEntry> entries = LoggerUtil.getAllByDates( user, startDate, endDate );
        return entries;
    }

}
