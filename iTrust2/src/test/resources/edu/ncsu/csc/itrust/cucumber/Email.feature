#Author plgiroua
Feature: Sending an email to users after password change/reset, appointment status is updated or account is locked out.
	I want the system to email users after a password change, appointment update or account lockout.
	
Scenario Outline: A logged-in user changes their password
	Given there is a user with <username> and with password <correct>
	When I login as <username> with password <password>
	Then I select change password
	And I enter my <correct> password and my <newpass> password twice
	Then I update my password successfully and an email is sent

Examples:
	|username|correct|newpass|
	|hcp     |123456 |654321 |
	
	
Scenario Outline: A logged-in user with no email changes their password
	Given there is a user with <username> and with password <correct>
	When I login as <username> with password <password>
	Then I select change password
	And I enter my <correct> password and my <newpass> password twice
	Then I update my password successfully but no email is sent

Examples:
	|username|correct|newpass|
	|hcp     |123456 |654321 |

	
Scenario Outline: A user resets their password
	Given there is a user with <username> on the login page
	When I select forgot password
	Then I enter my <username>
	Then I reset my password successfully and an email is sent

Examples:
	|username|
	|hcp     |
	
	
Scenario Outline: An hcp updates the status of an appointment
	Given there is an hcp with username <hcpuser> and password <password>
	When I login and view appointment requests
	Then I select the appointment for the patient with <username> and accept it
	Then the user is notified of the status update via email

Examples:
	|hcpuser|password|username|
	|hcp    |123456  |patient |
	|hcp    |123456  |jbean   |
	
