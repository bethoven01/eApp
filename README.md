# This is repository for CodeElan's UI and API test automation framework

1. Run the [RecordActions.java] with below command (src/test/java/com/codeelan/stepdefinitions/RecordActions.java)
2. Run the [RunInterfaceTest.java](src/test/java/com/codeelan/runner/RunInterfaceTest.java)
3. Run the mvn clean test
4. Run the mvn allure:serve

mvn exec:java -D exec.mainClass="com.codeelan.libraies.RecordActions" -D exec.args="Scenario2"
mvn exec:java -D exec.mainClass="com.codeelan.libraies.RecordActions" -D exec.args="Scenario2 https://www.saucedemo.com/"

