package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.BasicHealthMetrics;
import edu.ncsu.csc.itrust2.models.persistent.User;

public class BasicHealthMetricsTest {

    /**
     * Tests the functionality of BasicHealthMetrics hashing method.
     */
    @Test
    public void testBasicHealthMetricsHash () {
        // Initialize fields for BasicHealthMetrics
        final Integer dialostic = new Integer( 0 );
        final Integer hdl = new Integer( 0 );
        final Float height = new Float( 0 );
        final HouseholdSmokingStatus hss = HouseholdSmokingStatus.INDOOR;
        final Float headCircumference = new Float( 0 );
        final Integer ldl = new Integer( 0 );
        final PatientSmokingStatus pss = PatientSmokingStatus.FORMER;
        final Integer systolic = new Integer( 0 );
        final Integer tri = new Integer( 120 );
        final Float weight = new Float( 0 );
        final User hcp = new User( "bcmorri3", "12345", Role.ROLE_HCP, new Integer( 1 ) );
        final User patient = new User( "athoma10", "12345", Role.ROLE_PATIENT, new Integer( 0 ) );

        // Create new metric, m1.
        final BasicHealthMetrics m1 = new BasicHealthMetrics();
        m1.setDiastolic( dialostic );
        m1.setHdl( hdl );
        m1.setHeight( height );
        m1.setHouseSmokingStatus( hss );
        m1.setHeadCircumference( headCircumference );
        m1.setLdl( ldl );
        m1.setPatientSmokingStatus( pss );
        m1.setSystolic( systolic );
        m1.setTri( tri );
        m1.setWeight( weight );
        m1.setHcp( hcp );
        m1.setPatient( patient );

        // Create second metric, m2.
        final BasicHealthMetrics m2 = new BasicHealthMetrics();
        m2.setDiastolic( dialostic );
        m2.setHdl( hdl );
        m2.setHeight( height );
        m2.setHouseSmokingStatus( hss );
        m2.setHeadCircumference( headCircumference );
        m2.setLdl( ldl );
        m2.setPatientSmokingStatus( pss );
        m2.setSystolic( systolic );
        m2.setTri( tri );
        m2.setWeight( weight );
        m2.setHcp( hcp );
        m2.setPatient( patient );

        // Generate hash code for both objects.
        final int hash1 = m1.hashCode();
        final int hash2 = m2.hashCode();

        // Assert that they are equal metrics.
        assertEquals( hash1, hash2 );

    }

    /**
     * Tests the functionality of BasicHealthMetrics equals method.
     */
    @Test
    public void testBasicHealthMetricsEquals () {
        // Initialize fields for BasicHealthMetrics
        final Integer diastolic = new Integer( 0 );
        final Integer hdl = new Integer( 0 );
        final Float height = new Float( 0 );
        final HouseholdSmokingStatus hss = HouseholdSmokingStatus.INDOOR;
        final Float headCircumference = new Float( 0 );
        final Integer ldl = new Integer( 0 );
        final PatientSmokingStatus pss = PatientSmokingStatus.FORMER;
        final Integer systolic = new Integer( 0 );
        final Integer tri = new Integer( 120 );
        final Float weight = new Float( 0 );
        final User hcp = new User( "bcmorri3", "12345", Role.ROLE_HCP, new Integer( 1 ) );
        final User patient = new User( "athoma10", "12345", Role.ROLE_PATIENT, new Integer( 0 ) );

        // Create new metric, m1.
        final BasicHealthMetrics m1 = new BasicHealthMetrics();
        m1.setDiastolic( diastolic );
        m1.setHdl( hdl );
        m1.setHeight( height );
        m1.setHouseSmokingStatus( hss );
        m1.setHeadCircumference( headCircumference );
        m1.setLdl( ldl );
        m1.setPatientSmokingStatus( pss );
        m1.setSystolic( systolic );
        m1.setTri( tri );
        m1.setWeight( weight );
        m1.setHcp( hcp );
        m1.setPatient( patient );

        // Create second metric, m2.
        final BasicHealthMetrics m2 = new BasicHealthMetrics();
        m2.setDiastolic( diastolic );
        m2.setHdl( hdl );
        m2.setHeight( height );
        m2.setHouseSmokingStatus( hss );
        m2.setHeadCircumference( headCircumference );
        m2.setLdl( ldl );
        m2.setPatientSmokingStatus( pss );
        m2.setSystolic( systolic );
        m2.setTri( tri );
        m2.setWeight( weight );
        m2.setHcp( hcp );
        m2.setPatient( patient );

        // Create metric, m3.
        BasicHealthMetrics m3 = new BasicHealthMetrics();
        // Create metric, m3.
        BasicHealthMetrics m4 = new BasicHealthMetrics();

        // Initial assertions.
        // True Assertions!
        assertTrue( m1.equals( m1 ) );
        assertTrue( m1.equals( m2 ) );
        assertTrue( m3.equals( m4 ) );

        // False Assertions!
        assertFalse( m1.equals( null ) );
        assertFalse( m1.equals( hcp ) );
        assertFalse( m1.equals( m3 ) );
        assertFalse( m3.equals( m1 ) );

        // Now we branch.
        // Diastolic branches.
        m4.setDiastolic( diastolic );
        assertFalse( m3.equals( m4 ) );

        final Integer diastolic2 = new Integer( 2 );
        m3.setDiastolic( diastolic2 );
        assertFalse( m3.equals( m4 ) );

        // Refresh m3 and m4.
        m3 = new BasicHealthMetrics();
        m4 = new BasicHealthMetrics();

        // hcp branches
        m4.setHcp( hcp );
        assertFalse( m3.equals( m4 ) );

        m3.setHcp( new User( "bcmorri4", "12345", Role.ROLE_HCP, new Integer( 1 ) ) );
        assertFalse( m3.equals( m4 ) );

        // Refresh m3 and m4.
        m3 = new BasicHealthMetrics();
        m4 = new BasicHealthMetrics();

        // hdl branches
        m4.setHdl( hdl );
        assertFalse( m3.equals( m4 ) );

        m3.setHdl( new Integer( 1 ) );
        assertFalse( m3.equals( m4 ) );

        // Refresh m3 and m4.
        m3 = new BasicHealthMetrics();
        m4 = new BasicHealthMetrics();

        // head circumferences branches
        m4.setHeadCircumference( headCircumference );
        assertFalse( m3.equals( m4 ) );

        m3.setHeadCircumference( new Float( 1 ) );
        assertFalse( m3.equals( m4 ) );

        // Refresh m3 and m4.
        m3 = new BasicHealthMetrics();
        m4 = new BasicHealthMetrics();

        // height branches
        m4.setHeight( height );
        assertFalse( m3.equals( m4 ) );

        m3.setHeight( new Float( 1 ) );
        assertFalse( m3.equals( m4 ) );

        // Refresh m3 and m4.
        m3 = new BasicHealthMetrics();
        m4 = new BasicHealthMetrics();

        // hss branches
        m4.setHouseSmokingStatus( hss );
        assertFalse( m3.equals( m4 ) );
        m4 = new BasicHealthMetrics();

        // ldl branches
        m4.setLdl( ldl );
        assertFalse( m3.equals( m4 ) );

        m3.setLdl( new Integer( 1 ) );
        assertFalse( m3.equals( m4 ) );

        // Refresh m3 and m4.
        m3 = new BasicHealthMetrics();
        m4 = new BasicHealthMetrics();

        // patient branches
        m4.setPatient( patient );
        assertFalse( m3.equals( m4 ) );

        m3.setPatient( new User( "athoma11", "12345", Role.ROLE_PATIENT, new Integer( 0 ) ) );
        assertFalse( m3.equals( m4 ) );

        // Refresh m3 and m4.
        m3 = new BasicHealthMetrics();
        m4 = new BasicHealthMetrics();

        // pss branches
        m4.setPatientSmokingStatus( pss );
        assertFalse( m3.equals( m4 ) );
        m4 = new BasicHealthMetrics();

        // systolic branches
        m4.setSystolic( systolic );
        assertFalse( m3.equals( m4 ) );

        m3.setSystolic( new Integer( 1 ) );
        assertFalse( m3.equals( m4 ) );

        // Refresh m3 and m4.
        m3 = new BasicHealthMetrics();
        m4 = new BasicHealthMetrics();

        // tri branches
        m4.setTri( tri );
        assertFalse( m3.equals( m4 ) );

        m3.setTri( new Integer( 121 ) );
        assertFalse( m3.equals( m4 ) );

        // Refresh m3 and m4.
        m3 = new BasicHealthMetrics();
        m4 = new BasicHealthMetrics();

        // weight branches
        m4.setWeight( weight );
        assertFalse( m3.equals( m4 ) );

        m3.setWeight( new Float( 1 ) );
        assertFalse( m3.equals( m4 ) );
    }
}
