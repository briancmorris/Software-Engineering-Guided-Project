package edu.ncsu.csc.itrust2.config;

import java.io.IOException;
import java.util.Calendar;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.LoginAttempt;
import edu.ncsu.csc.itrust2.models.persistent.LoginBan;
import edu.ncsu.csc.itrust2.models.persistent.LoginLockout;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.Personnel;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.EmailUtil;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Custom AuthenticationFailureHandler to record Failed attempts, and lockout or
 * ban a user or IP if necessary.
 *
 * @author Thomas
 *
 */
public class FailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure ( final HttpServletRequest request, final HttpServletResponse response,
            final AuthenticationException ae ) throws IOException, ServletException {
        /*
         * Credit for username to
         * https://stackoverflow.com/questions/8676206/how-can-i-get-the
         * -username-from-a-failed-login-using-spring-security
         */
        final String username = request
                .getParameter( UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY );
        User user = null;
        final String addr = request.getRemoteAddr();

        if ( ae instanceof BadCredentialsException ) {
            // need to lockout IP
            if ( LoginAttempt.getIPFailures( addr ) >= 5 ) {
                LoginAttempt.clearIP( addr );
                // Check if need to ban IP
                if ( LoginLockout.getRecentIPLockouts( addr ) >= 2 ) {
                    // BAN
                    final LoginBan ban = new LoginBan();
                    ban.setIp( addr );
                    ban.setTime( Calendar.getInstance() );
                    ban.save();
                    LoginLockout.clearIP( addr );
                    LoggerUtil.log( TransactionType.IP_BANNED, addr, null, addr + " has been banned." );
                    this.getRedirectStrategy().sendRedirect( request, response, "/login?ipbanned" );
                }
                else {
                    // lockout IP.
                    final LoginLockout lockout = new LoginLockout();
                    lockout.setIp( addr );
                    lockout.setTime( Calendar.getInstance() );
                    lockout.save();
                    LoggerUtil.log( TransactionType.IP_LOCKOUT, addr, null, addr + " has been locked out for 1 hour." );
                    this.getRedirectStrategy().sendRedirect( request, response, "/login?iplocked" );

                }
                return;
            }
            else {
                // fail for IP
                final LoginAttempt attempt = new LoginAttempt();
                attempt.setTime( Calendar.getInstance() );
                attempt.setIp( addr );
                attempt.save();
            }

            // check username
            if ( username != null ) {
                user = User.getByName( username );
            }

            if ( user != null ) {
                // check if need to lockout username
                if ( LoginAttempt.getUserFailures( user ) >= 2 ) {
                    LoginAttempt.clearUser( user );
                    // check if need to ban user
                    if ( LoginLockout.getRecentUserLockouts( user ) >= 2 ) {
                        LoginLockout.clearUser( user );
                        final LoginBan ban = new LoginBan();
                        ban.setTime( Calendar.getInstance() );
                        ban.setUser( user );
                        ban.save();
                        LoggerUtil.log( TransactionType.USER_BANNED, username, null, username + " has been banned." );

                        sendLockoutEmail( user, true );

                        this.getRedirectStrategy().sendRedirect( request, response, "/login?banned" );
                    }
                    else {
                        // lockout user
                        final LoginLockout lock = new LoginLockout();
                        lock.setTime( Calendar.getInstance() );
                        lock.setUser( user );
                        lock.save();
                        LoggerUtil.log( TransactionType.USER_LOCKOUT, username, null,
                                username + " has been locked out for 1 hour." );

                        sendLockoutEmail( user, false );

                        this.getRedirectStrategy().sendRedirect( request, response, "/login?locked" );
                    }
                    return;
                }
                else {
                    // fail for username
                    final LoginAttempt attempt = new LoginAttempt();
                    attempt.setTime( Calendar.getInstance() );
                    attempt.setUser( user );
                    attempt.save();
                }
            }

        }
        else if ( ae instanceof DisabledException ) {
            if ( username != null ) {
                user = User.getByName( username );
            }
            if ( user != null ) {
                // redirect to user lockout or user ban
                if ( LoginBan.isUserBanned( user ) ) {
                    this.getRedirectStrategy().sendRedirect( request, response, "/login?banned" );
                    return;
                }
                else if ( LoginLockout.isUserLocked( user ) ) {
                    this.getRedirectStrategy().sendRedirect( request, response, "/login?locked" );
                    return;
                }
                // else, otherwise disabled
            }

            this.getRedirectStrategy().sendRedirect( request, response, "/login?locked" );
            return;
        }
        this.getRedirectStrategy().sendRedirect( request, response, "/login?error" );
    }

    /**
     * Sends the lockout/ban email to users
     *
     * @param user
     *            the user to send the email to
     * @param isBanned
     *            true if the user has been banned, false if locked out
     */
    private void sendLockoutEmail ( final User user, final boolean isBanned ) {
        try {
            final String userEmail = EmailUtil.getUserEmail( user );
            if ( userEmail == null ) {
                LoggerUtil.log( TransactionType.EMAIL_NOT_SENT, user.getUsername(), null, "Unable to send email to "
                        + user.getUsername() + " for " + ( isBanned ? "ban" : "lockout" ) + " notification." );
            }
            else {
                final Patient pat = Patient.getPatient( user );
                final Personnel per = Personnel.getByName( user );
                final String firstName = pat != null ? pat.getFirstName() : per.getFirstName();

                String body = "Hello " + firstName + ",\n\nYour account has been ";
                body += ( isBanned
                        ? "banned due to your 3 lockouts. Please contact a system administrator to regain access to the system."
                        : "locked out due to your 3 failed login attempts. Please wait an hour before attempting to login again." );
                body += "\n\n--The iTrust2 Team";

                EmailUtil.sendEmail( userEmail, "iTrust2 Account " + ( isBanned ? "Ban" : "Lockout" ), body );
                LoggerUtil.log( TransactionType.EMAIL_ACCOUNT_LOCKOUT, user.getUsername(), null,
                        "Email sent to " + userEmail + " for " + ( isBanned ? "ban" : "lockout" ) + " notification." );
            }
        }
        catch ( final MessagingException e ) {
            LoggerUtil.log( TransactionType.EMAIL_NOT_SENT, user.getUsername(), null, "Unable to send email to "
                    + user.getUsername() + " for " + ( isBanned ? "ban" : "lockout" ) + " notification." );
        }
    }
}
