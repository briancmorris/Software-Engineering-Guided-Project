#Author athoma10
Feature: View Access Log 
	As a patient
	I want to view the home access log
	So that I can check the log entries appear


Scenario: Check patient logs
	Given A patient already exists in the system
	When I Log in as an existing patient
	Then The home page has log entries
	


