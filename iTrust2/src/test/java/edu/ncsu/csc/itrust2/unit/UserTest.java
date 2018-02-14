package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Unit tests for the User class.
 *
 * @author jshore
 *
 */
public class UserTest {

    /**
     * Tests equals comparison of two user objects. Also verifies getters and
     * setters of the used properties.
     */
    @Test
    public void testEqualsAndProperties () {
        final User u1 = new User();
        final User u2 = new User();

        assertFalse( u1.equals( new Object() ) );
        assertFalse( u1.equals( null ) );
        assertTrue( u1.equals( u1 ) );

        u1.setEnabled( 1 );
        assertTrue( 1 == u1.getEnabled() );
        assertFalse( u1.equals( u2 ) );
        assertFalse( u2.equals( u1 ) );
        u2.setEnabled( 1 );

        u1.setPassword( "abcdefg" );
        assertEquals( "abcdefg", u1.getPassword() );
        assertFalse( u1.equals( u2 ) );
        assertFalse( u2.equals( u1 ) );
        u2.setPassword( "abcdefg" );

        u1.setRole( Role.valueOf( "ROLE_PATIENT" ) );
        assertEquals( Role.valueOf( "ROLE_PATIENT" ), u1.getRole() );
        assertFalse( u1.equals( u2 ) );
        assertFalse( u2.equals( u1 ) );
        u2.setRole( Role.valueOf( "ROLE_PATIENT" ) );

        u1.setUsername( "abcdefg" );
        assertEquals( "abcdefg", u1.getUsername() );
        assertFalse( u1.equals( u2 ) );
        assertFalse( u2.equals( u1 ) );
        u2.setUsername( "abcdefg" );

        assertTrue( u1.equals( u2 ) );

        final List<User> adminList = User.getByRole( Role.ROLE_ADMIN );
        if ( adminList.size() > 0 ) {
            assertTrue( adminList.get( 0 ).equals( adminList.get( 0 ) ) );
        }

        final List<User> hcpList = User.getHCPs();
        if ( hcpList.size() > 0 ) {
            assertTrue( hcpList.get( 0 ).equals( hcpList.get( 0 ) ) );
        }

        final List<User> patList = User.getPatients();
        if ( patList.size() > 0 ) {
            assertTrue( patList.get( 0 ).equals( patList.get( 0 ) ) );
        }
    }

}
