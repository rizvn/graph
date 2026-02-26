package com.github.rizvn.graph;

import org.junit.jupiter.api.Test;

/**
 * Test cases for the Graph library.
 */
public class GraphTest {

    /**
     * Example graph class for testing.
     */
    static class ExampleGraph extends Graph {
        private int counter;

        public void init() {
            // Initialize the graph
            initGraph();

            // On start run Step1
            stepNamed(START, this::step1)
                    // Then goto step 2
                    .whenNamed(DEFAULT, "Step2", "Proceed to Step 2");

            // Define Step2
            step("Step2", this::step2)
                    // After step 2, if Step2RepeatCondition is true goto Step2
                    .whenNamed(this::step2RepeatCondition, "Step2", "")
                    // After step 2, if Step2ContinueCondition is true goto Step3
                    .whenNamed(this::step2ContinueCondition, "Step3", "Repeat step 3 until counter is 3");

            // Define Step3
            step("Step3", this::step3);
        }

        // Step1 logic
        public void step1() {
            System.out.println("Counter initialised to 0");
            counter = 0;
        }

        // Step2 logic
        public void step2() {
            System.out.println("Increment counter");
            counter++;
            System.out.printf("Counter: %d%n", counter);
        }

        // Condition to continue from Step2
        public boolean step2ContinueCondition() {
            return counter >= 3;
        }

        // Condition to repeat Step2
        public boolean step2RepeatCondition() {
            return counter < 3;
        }

        // Step3 logic
        public void step3() {
            System.out.println("Reached Step 3, Graph execution complete.");
        }
    }

    @Test
    public void testRunGraph() {
        System.out.println("=== Test: Run Graph ===");
        ExampleGraph exampleGraph = new ExampleGraph();
        exampleGraph.init();
        exampleGraph.runGraph();
    }

    @Test
    public void testRunGraphWithHooks() {
        System.out.println("\n=== Test: Run Graph with Hooks ===");
        ExampleGraph exampleGraph = new ExampleGraph();
        exampleGraph.init();

        exampleGraph.setHookBeforeStep(stepName ->
                System.out.printf("Before executing step: %s%n", stepName));

        exampleGraph.setHookAfterStep(stepName ->
                System.out.printf("After executing step: %s%n", stepName));

        exampleGraph.setHookBeforeTransition((from, to, desc) ->
                System.out.printf("Transitioning from %s to %s  Desc: %s%n", from, to, desc));

        exampleGraph.runGraph();
    }

    @Test
    public void testOverrideStartGraphAtDifferentStep() {
        System.out.println("\n=== Test: Override Start graph at a different step ===");
        ExampleGraph exampleGraph = new ExampleGraph();
        exampleGraph.init();

        // Since we are skipping step 1, initialise counter here
        exampleGraph.counter = 0;

        // Override starting point to Step2
        exampleGraph.setStepToRun("Step2");

        exampleGraph.setHookBeforeStep(stepName ->
                System.out.printf("Before executing step: %s%n", stepName));

        exampleGraph.runGraph();
    }

    @Test
    public void testVisualiseGraphAsMermaidChart() {
        System.out.println("\n=== Test: Visualise graph as mermaid chart ===");
        ExampleGraph exampleGraph = new ExampleGraph();
        exampleGraph.init();
        String chart = exampleGraph.toString();

        System.out.printf("Mermaid Chart:%n%s", chart);
    }
}

