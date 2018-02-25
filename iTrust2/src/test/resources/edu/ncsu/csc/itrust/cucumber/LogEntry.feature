#Author athoma10
Feature: View Access Log 
	As a user
	I want to view the home access log
	So that I can check the log entries appear


Scenario: Check patient logs
	Given A patient already exists in the system
	When I Log in as an existing patient
	Then The home page has 1 log entries
	
Scenario: Check admin logs
	Given A admin already exists in the system
	When I Log in as an existing admin
	Then The home page has 1 log entries

Scenario: Check hcp logs
	Given A hcp already exists in the system
	When I Log in as an existing hcp
	Then The home page has 1 log entries
	
Scenario: Check patient has over 10 logs
	Given Previous patient still exists in the system
	When I Log in as an existing patient
	When I make 10 appointment and go to home page
	Then The home page has 10 log entries

Scenario: Check admin has over 10 logs
	Given Previous admin still exists in the system
	When I Log in as an existing admin
	When I add 10 hospitals and go to home page
	Then The home page has 10 log entries






