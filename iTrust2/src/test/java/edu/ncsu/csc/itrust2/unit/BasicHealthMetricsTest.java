package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.itrust2.models.persistent.BasicHealthMetrics;

public class BasicHealthMetricsTest {

    /**
     * Tests the functionality of BasicHealthMetrics hashing method.
     */
    @Test
    public void testBasicHealthMetricsHash () {
        /**
         * final Integer dialostic, final Integer hdl, final Float height, final
         * HouseholdSmokingStatus hss, final Float headCircumference, final
         * Integer ldl, final PatientSmokingStatus pss, final Integer systolic,
         * final Integer tri, final Float weight, final User hcp, final User
         * patient
         */

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
        // final User hcp = new User( "bcmorri3", "12345", null, new Integer( 1
        // ) );
        // final User patient = new User( "athoma10", "12345", null, new
        // Integer( 0 ) );

        // Create new metric.
        final BasicHealthMetrics m1 = new BasicHealthMetrics( dialostic, hdl, height, hss, headCircumference, ldl, pss,
                systolic, tri, weight );

        // Create second metric.
        final BasicHealthMetrics m2 = new BasicHealthMetrics( dialostic, hdl, height, hss, headCircumference, ldl, pss,
                systolic, tri, weight );

        // Generate hash code for both objects.
        final int hash1 = m1.hashCode();
        final int hash2 = m2.hashCode();

        assertEquals( hash1, hash2 );

    }
}
