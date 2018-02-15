package edu.ncsu.csc.itrust2.controllers.api;

import java.net.InetAddress;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.personnel.PasswordChangeForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.PasswordResetToken;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.Personnel;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.EmailUtil;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * REST Controller that provides the endpoints for password changing and
 * resetting.
 *
 * @author Kai Presler-Marshall
 * @author Thomas
 *
 */
@RestController
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class APIPasswordController extends APIController {

    /** Password encoder instance */
    static PasswordEncoder pe = new BCryptPasswordEncoder();

    @Autowired
    Environment            environment;

    /**
     * Used when an authenticated user fills in the Change Password form.
     *
     * @param form
     *            The ChangePassword form the user filled out.
     * @return The REST response
     */
    @PostMapping ( BASE_PATH + "/changePassword" )
    public ResponseEntity changePassword ( @RequestBody final PasswordChangeForm form ) {
        final String name = SecurityContextHolder.getContext().getAuthentication().getName();
        final User user = User.getByName( name );
        if ( user == null ) {
            return new ResponseEntity( errorResponse( "Could not find user with name " + name ),
                    HttpStatus.BAD_REQUEST );
        }
        try {
            if ( form.validateChange( user ) ) {
                user.setPassword( pe.encode( form.getNewPassword() ) );
                user.save();
                LoggerUtil.log( TransactionType.PASSWORD_UPDATE_SUCCESS, user.getUsername(),
                        "Successfully changed password for user " + user.getUsername() );

                sendPasswordEmail( user );

                return new ResponseEntity( successResponse( "Password changed successfully" ), HttpStatus.OK );
            }

            LoggerUtil.log( TransactionType.PASSWORD_UPDATE_FAILURE, user.getUsername(),
                    "Could not change password for user " + user.getUsername() );
            return new ResponseEntity( errorResponse( "Failed to change password" ), HttpStatus.BAD_REQUEST );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.PASSWORD_UPDATE_FAILURE, user.getUsername(),
                    "Could not change password for user " + user.getUsername() );
            return new ResponseEntity(
                    errorResponse(
                            "Could not change password for " + user.getUsername() + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Sends an email to a user after a successful password change or reset
     *
     * @param user
     *            the user to send the email to
     */
    private void sendPasswordEmail ( final User user ) {
        try {
            final String userEmail = EmailUtil.getUserEmail( user );
            if ( userEmail == null ) {
                LoggerUtil.log( TransactionType.EMAIL_NOT_SENT, user.getUsername(), null,
                        "Unable to send email to " + user.getUsername() + " for password change notification." );
            }
            else {
                String body = "Hello " + user.getUsername() + ",\n\nYour password has been successfully changed. ";
                body += "If you did not change your password, please contact an administrator immediately.";
                body += "\n\n--iTrust2 Admin";

                EmailUtil.sendEmail( userEmail, "iTrust2 Password Change", body );
                LoggerUtil.log( TransactionType.EMAIL_PASSWORD_CHANGE, user.getUsername(), null,
                        "Email sent to " + userEmail + " for password change notification." );
            }
        }
        catch ( final MessagingException e ) {
            LoggerUtil.log( TransactionType.EMAIL_NOT_SENT, user.getUsername(), null,
                    "Unable to send email to " + user.getUsername() + " for password change notification." );
        }
    }

    /**
     * Used by an unauthenticated user to request a password reset. Sends an
     * email to the address registered with the provided username.
     *
     * @param username
     *            The username of the requested reset.
     * @return The REST response
     */
    @PostMapping ( BASE_PATH + "/requestPasswordReset" )
    public ResponseEntity requestReset ( @RequestBody final String username ) {
        final User user = User.getByName( username );
        if ( user == null ) {
            return new ResponseEntity( errorResponse( "Could not find user with username " + username ),
                    HttpStatus.BAD_REQUEST );
        }
        try {
            final PasswordResetToken token = new PasswordResetToken( user );
            token.save();
            final String port = "8080";
            final String host = InetAddress.getLocalHost().getHostAddress();

            final String link = "http://" + host + ":" + port + "/iTrust2/resetPassword?tkid=" + token.getId();

            String addr = "";
            String firstName = "";
            final Personnel person = Personnel.getByName( user );
            if ( person != null ) {
                addr = person.getEmail();
                firstName = person.getFirstName();
            }
            else {
                final Patient patient = Patient.getPatient( user );
                if ( patient != null ) {
                    addr = patient.getEmail();
                    firstName = patient.getFirstName();
                }
                else {
                    throw new Exception( "No Patient or Personnel on file for " + user.getId() );
                }
            }

            String body = "Hello " + firstName + ", \n\nWe receieved a request to reset your password.\n";
            body += "Go to " + link + "\nand use the reset token: " + token.getTempPasswordPlaintext() + "\n";
            body += "\nIf you did not request a password reset, please contact a system administrator.\n\n--iTrust2 Admin";
            EmailUtil.sendEmail( addr, "iTrust2 Password Reset", body );

            LoggerUtil.log( TransactionType.PASSWORD_UPDATE_SUCCESS, user.getUsername(),
                    "Successfully changed password for user " + user.getUsername() );
            return new ResponseEntity( successResponse( "" ), HttpStatus.OK );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            LoggerUtil.log( TransactionType.PASSWORD_UPDATE_FAILURE, user.getUsername(),
                    "Could not change password for user with username " + username );
            return new ResponseEntity( errorResponse( "Could not complete request due to: " + e.getMessage() ),
                    HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }

    /**
     * Used by an unauthenticated user on the reset password page. The temporary
     * password must be in the current field of the change password form.
     *
     * @param form
     *            The PasswordChangeForm filled out by the user
     * @param tokenId
     *            The reset token id
     * @return the REST response
     */
    @PostMapping ( BASE_PATH + "/resetPassword/{tokenId}" )
    public ResponseEntity resetPassword ( @RequestBody final PasswordChangeForm form,
            @PathVariable final long tokenId ) {
        final PasswordResetToken token = PasswordResetToken.getById( tokenId );
        if ( token == null ) {
            return new ResponseEntity( errorResponse( "Invalid reset token" ), HttpStatus.BAD_REQUEST );
        }

        final User user = token.getUser();
        try {
            if ( form.validateReset( token ) ) {
                user.setPassword( pe.encode( form.getNewPassword() ) );
                user.save();
                token.delete();

                LoggerUtil.log( TransactionType.PASSWORD_UPDATE_SUCCESS, user.getUsername(),
                        "Successfully changed password for user " + user.getUsername() );

                sendPasswordEmail( user );

                return new ResponseEntity( successResponse( "Passsword changed successfully" ), HttpStatus.OK );
            }
            LoggerUtil.log( TransactionType.PASSWORD_UPDATE_FAILURE, user.getUsername(),
                    "Could not change password for user with username " + user.getUsername() );
            return new ResponseEntity( errorResponse( "Failed to reset password" ), HttpStatus.BAD_REQUEST );
        }
        catch ( final IllegalArgumentException e ) {
            LoggerUtil.log( TransactionType.PASSWORD_UPDATE_FAILURE, user.getUsername(),
                    "Could not change password for user with username " + user.getUsername() );
            return new ResponseEntity( errorResponse( "Could not change password for user with username "
                    + user.getUsername() + " because of " + e.getMessage() ), HttpStatus.BAD_REQUEST );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.PASSWORD_UPDATE_FAILURE, user.getUsername(),
                    "Could not change password for user with username " + user.getUsername() );
            return new ResponseEntity( errorResponse( "Unable to reset password due to: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

}
