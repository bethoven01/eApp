Feature: 

	

	@API-scenario2
	Scenario: scenario2 - 
		Given User is on login page for TestCase "API-scenario2"
		When Call API 1 "https://automationintesting.online/" request "GET" URL with following parameters
		 | endpoint | JSON |
		 | params | JSON |
		 | auth | JSON |
		 | headers | JSON |
		 | body | JSON |
		 | script | JSON |
		 | field | JSON |
		Then Verify status of preceding request 1 is 200
		When Call API 2 "https://automationintesting.online/branding/" request "GET" URL with following parameters
		 | endpoint | JSON |
		 | params | JSON |
		 | auth | JSON |
		 | headers | JSON |
		 | body | JSON |
		 | script | JSON |
		 | field | JSON |
		Then Verify status of preceding request 2 is 200
		Then Verify schema of preceding response 2 is "testdata\Responses\scenario2\API_ResponseSchema2.json"
		When Call API 3 "https://automationintesting.online/room/" request "GET" URL with following parameters
		 | endpoint | JSON |
		 | params | JSON |
		 | auth | JSON |
		 | headers | JSON |
		 | body | JSON |
		 | script | JSON |
		 | field | JSON |
		Then Verify status of preceding request 3 is 200
		Then Verify schema of preceding response 3 is "testdata\Responses\scenario2\API_ResponseSchema3.json"
		When Call API 4 "https://automationintesting.online/message/" request "POST" URL with following parameters
		 | endpoint | JSON |
		 | params | JSON |
		 | auth | JSON |
		 | headers | JSON |
		 | body | JSON |
		 | script | JSON |
		 | field | JSON |
		Then Verify status of preceding request 4 is 201
		Then Verify schema of preceding response 4 is "testdata\Responses\scenario2\API_ResponseSchema4.json"
