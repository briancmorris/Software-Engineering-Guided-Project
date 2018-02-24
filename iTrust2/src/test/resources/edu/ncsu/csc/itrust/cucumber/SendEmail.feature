#Author plgiroua
Feature: Sending an email to users after password change/reset, appointment status is updated or account is locked out.
	I want the system to email users after a password change, appointment update or account lockout.
	
Scenario Outline: A logged-in user changes their password
	Given I login as <username> with password <password>
	Then I select change password
	And I enter my password <password> and my <newpass> password twice
	Then I update my password successfully <username>
	And an email is sent to <username>

Examples:
	|username|password|newpass|
	|hcp     |123456  |654321 |
	
	
Scenario Outline: A logged-in user with no email changes their password
	Given I login as <username> with password <password>
	Then I select change password
	And I enter my password <password> and my <newpass> password twice
	Then I update my password successfully <username>
	And no email is sent to <username>

Examples:
	|username|password|newpass|
	|patient |123456  |654321 |

Scenario Outline: A user resets their password
    Given I am on the login page
    When I select forgot password
    Then I enter my username <username>
    Then I reset my password successfully for <username>
    And an email is sent to <username>

Examples:
    |username|
    |hcp     |
    
Scenario Outline: A user fails to reset their password
    Given I am on the login page
    When I select forgot password
    Then I enter my username <username>
    Then I do not reset my password successfully for <username>
    And no email is sent to <username>

Examples:
    |username|
    |patient |