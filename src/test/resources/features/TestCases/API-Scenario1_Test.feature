Feature: 

	

	@API-Scenario1
	Scenario: Scenario1 - 
		Given User is on login page for TestCase "API-Scenario1"
		When Call API 1 "https://www.saucedemo.com/" request "GET" URL with following parameters
		 | endpoint | JSON |
		 | params | JSON |
		 | auth | JSON |
		 | headers | JSON |
		 | body | JSON |
		 | script | JSON |
		 | field | JSON |
		Then Verify status of preceding request 1 is 200
		When Call API 2 "https://www.saucedemo.com/manifest.json" request "GET" URL with following parameters
		 | endpoint | JSON |
		 | params | JSON |
		 | auth | JSON |
		 | headers | JSON |
		 | body | JSON |
		 | script | JSON |
		 | field | JSON |
		Then Verify status of preceding request 2 is 200
		Then Verify schema of preceding response 2 is "testdata\Responses\Scenario1\API_ResponseSchema2.json"
