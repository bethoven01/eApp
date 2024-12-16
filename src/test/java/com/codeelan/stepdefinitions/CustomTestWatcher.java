package com.codeelan.stepdefinitions;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class CustomTestWatcher {

    @Rule
    public TestWatcher testWatcher = new TestWatcher() {

        @Override
        protected void succeeded(Description description) {
            System.out.println(description.getDisplayName() + " passed.");
            // You can add logic here to send the result to an API or perform other actions
        }

        @Override
        protected void failed(Throwable e, Description description) {
            System.out.println(description.getDisplayName() + " failed.");
            // You can add logic here to send the result to an API or perform other actions
        }

        @Override
        protected void skipped(org.junit.AssumptionViolatedException e, Description description) {
            System.out.println(description.getDisplayName() + " skipped.");
            // You can add logic here to send the result to an API or perform other actions
        }
    };

    @Test
    public void successfulTest() {
        // This test will pass
        System.out.println("This test is successful.");
    }

    @Test
    public void failingTest() {
        // This test will fail
        System.out.println("This test is failing.");
        throw new RuntimeException("Failing the test intentionally.");
    }

    @Test
    public void skippedTest() {
        // This test will be skipped
        org.junit.Assume.assumeTrue(false); // Assuming false makes it skip
    }
}
