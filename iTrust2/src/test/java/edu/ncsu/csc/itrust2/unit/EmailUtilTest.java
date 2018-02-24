package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.itrust2.apitest.TestUtils;
import edu.ncsu.csc.itrust2.config.RootConfiguration;
import edu.ncsu.csc.itrust2.forms.admin.UserForm;
import edu.ncsu.csc.itrust2.forms.personnel.PersonnelForm;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.persistent.Personnel;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;
import edu.ncsu.csc.itrust2.utils.EmailUtil;

@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class EmailUtilTest {

    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up test
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
    }

    /**
     * Tests the getUserEmail functionality
     */
    @Test
    public void testGetUserEmail () {
        User u = null;
        assertNull( EmailUtil.getUserEmail( u ) );

        u = new User();
        assertNull( EmailUtil.getUserEmail( u ) );

        // // makes several patients with valid emails
        // HibernateDataGenerator.generateTestFaculties();
        //
        // // tests patient
        // final Patient p = Patient.getPatient( "AliceThirteen" );
        // assertEquals( "csc326s100x@gmail.com", EmailUtil.getUserEmail(
        // p.getSelf() ) );

        // tests hcp
        u = User.getByName( "hcp" );
        assertEquals( "csc326.201.1@gmail.com", EmailUtil.getUserEmail( u ) );
    }

    /**
     * Tests the sendEmail functionality
     */
    @WithMockUser ( username = "bob", roles = { "USER", "PATIENT" } )
    @Test
    public void testSendEmail () throws Exception {
        final UserForm patient = new UserForm( "bob", "cats", Role.ROLE_PATIENT, 1 );

        User user = new User( patient );
        user.save();

        user = User.getByName( "bob" ); // ensure they exist

        final PersonnelForm personnel = new PersonnelForm();
        personnel.setAddress1( "1 Test Street" );
        personnel.setAddress2( "Address Part 2" );
        personnel.setCity( "Prag" );
        personnel.setEmail( "csc326s100x@gmail.com" );
        personnel.setFirstName( "Test" );
        personnel.setLastName( "HCP" );
        personnel.setPhone( "123-456-7890" );
        personnel.setSelf( user.getUsername() );
        personnel.setState( State.NC.toString() );
        personnel.setZip( "27514" );
        mvc.perform( post( "/api/v1/personnel" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( personnel ) ) );

        assertEquals( Personnel.getByName( user ).getEmail(), EmailUtil.getUserEmail( user ) );
        assertNotNull( EmailUtil.getUserEmail( user ) );

        EmailUtil.sendEmail( EmailUtil.getUserEmail( user ), "Test SendEmail", "body" );
    }

}
