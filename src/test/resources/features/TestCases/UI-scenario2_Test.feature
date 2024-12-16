Feature: 

	

	@UI-scenario2
	Scenario: scenario2 - 
		Given User is on login page for TestCase "UI-scenario2"
		When Open page "https://automationintesting.online"
		Then Verify Page Title is "Restful-booker-platform demo"
		Then Verify Default Value of "text" having "id" "name" is ""
		Then Verify Placeholder of "text" having "id" "name" is "Name"
		Then Enter value "Gaurav Khandelwal" in "text" having "id" "name"
		Then Verify Default Value of "text" having "id" "email" is ""
		Then Verify Placeholder of "text" having "id" "email" is "Email"
		Then Enter value "gaurav.khandelwal@codeelan.com" in "text" having "id" "email"
		Then Verify Default Value of "text" having "id" "phone" is ""
		Then Verify Placeholder of "text" having "id" "phone" is "Phone"
		Then Enter value "1234567890123457890" in "text" having "id" "phone"
		Then Verify Default Value of "text" having "id" "subject" is ""
		Then Verify Placeholder of "text" having "id" "subject" is "Subject"
		Then Verify Default Value of "text" having "id" "subject" is ""
		Then Verify Placeholder of "text" having "id" "subject" is "Subject"
		Then Enter value "Test Case 1 Test Case 1 Test Case 1 Test Case 1 Test Case 1 Test Case 1" in "text" having "id" "subject"
		Then Verify Default Value of "textarea" having "id" "description" is ""
		Then Enter value "Test Case 1 Test Case 1 Test Case 1 Test Case 1 Test Case 1 Test Case 1 Test Case 1 Test Case 1 Test Case 1 Test Case 1 Test Case 1 Test Case 1 Test Case 1 Test Case 1 Test Case 1 Test Case 1 Test Case 1 Test Case 1 Test Case 1 Test Case 1 Test Case 1 Test Case 1 Test Case 1 Test Case 1" in "textarea" having "id" "description"
		Then Click element "button" having "id" "submitContact"
