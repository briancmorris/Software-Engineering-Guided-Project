#Author athoma10

Feature: View Access Log
	As a Patient
	I want to view the access log
	So that I can maintain confidentiality and integrity of my data

Scenario: View Access Log with less than 10 transactions after Login
Given There is a sample Patient in the database
Given I log in as patient
Given I log out 
Given I log in as patient
Given I log out 
Given I log in as patient
Given I log out 
Given I log in as patient
Then the access log has 7 log transactions

Scenario: View Access Log with 10 transactions after Login
Given There is a sample Patient in the database
Given I log in as patient
Given I log out 
Given I log in as patient
Given I log out 
Given I log in as patient
Given I log out 
Given I log in as patient
Given I log out
Given I log in as patient
Given I navigate to the home page
Then the access log has 10 log transactions

Scenario: View Access Log with more than 10 transactions after Login
Given There is a sample Patient in the database
Given I log in as patient
Given I log out 
Given I log in as patient
Given I log out 
Given I log in as patient
Given I log out 
Given I log in as patient
Given I log out
Given I log in as patient
Given I log out
Given I log in as patient
Given I log out
Given I log in as patient
Then the access log has 10 log transactions

Scenario: View Access Log with less than 10 transactions in separate page
Given There is a sample Patient in the database
Given I log in as patient
Given I log out 
Given I log in as patient
Given I log out 
Given I log in as patient
Given I log out 
Given I log in as patient
Given I navigate to view the access log on a separate page
Then the access log has 7 log transactions

Scenario: View Access Log with more than 10 transactions in separate page
Given There is a sample Patient in the database
Given I log in as patient
Given I log out 
Given I log in as patient
Given I log out 
Given I log in as patient
Given I log out 
Given I log in as patient
Given I log out
Given I log in as patient
Given I log out
Given I log in as patient
Given I navigate to view the access log on a separate page
Then the access log has 11 log transactions
	