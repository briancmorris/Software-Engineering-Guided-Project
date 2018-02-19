package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.hcp.OfficeVisitForm;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.OfficeVisit;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Tests the OfficeVisitForm class
 *
 * @author Peter Girouard
 */
public class OfficeVisitFormTest {

    /**
     * Tests the creation of an office visit form from an office visit object
     */
    @SuppressWarnings ( "deprecation" )
    @Test
    public void testOfficeVisitForm () {
        final OfficeVisit visit = new OfficeVisit();
        visit.setPatient( new User( "bob", "123456", Role.ROLE_PATIENT, 1 ) );
        visit.setHcp( new User( "mark", "!123456", Role.ROLE_HCP, 1 ) );
        final Calendar c = Calendar.getInstance();
        c.setTime( new Date( 2017, 12, 25, 9, 30, 0 ) );
        visit.setDate( c );
        visit.setNotes( "Patient had an appt" );
        visit.setId( (long) 2453453 );
        visit.setPrescriptions( new ArrayList<Prescription>() );

        final OfficeVisitForm form = new OfficeVisitForm( visit );
        assertEquals( visit.getPatient().getUsername(), form.getPatient() );
        assertEquals( visit.getHcp().getUsername(), form.getHcp() );
        assertEquals( visit.getNotes(), form.getNotes() );
    }

}
