# Java Graph Library

A library for graph-based programming in Java, converted from the original Go implementation.

## Installation

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.github.rizvn</groupId>
    <artifactId>graph</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Building from Source

```bash
mvn clean install
```

## Usage

```java
import com.github.rizvn.graph.Graph;

// Create a graph class for your workflow
class ExampleGraph extends Graph {
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
                // After step 2, if step2RepeatCondition is true goto Step2
                .whenNamed(this::step2RepeatCondition, "Step2", "")
                // After step 2, if step2ContinueCondition is true goto Step3
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
```

To run the graph:

```java
ExampleGraph exampleGraph = new ExampleGraph();
exampleGraph.init();
exampleGraph.runGraph();
```

## Features

### Hooks

You can add hooks to monitor graph execution:

```java
ExampleGraph exampleGraph = new ExampleGraph();
exampleGraph.init();

exampleGraph.setHookBeforeStep(stepName ->
        System.out.printf("Before executing step: %s%n", stepName));

exampleGraph.setHookAfterStep(stepName ->
        System.out.printf("After executing step: %s%n", stepName));

exampleGraph.setHookBeforeTransition((from, to, desc) ->
        System.out.printf("Transitioning from %s to %s  Desc: %s%n", from, to, desc));

exampleGraph.runGraph();
```

### Override Starting Step

Useful for resuming graphs from a specific step:

```java
ExampleGraph exampleGraph = new ExampleGraph();
exampleGraph.init();

// Override starting point to Step2
exampleGraph.setStepToRun("Step2");
exampleGraph.runGraph();
```

### Visualize as Mermaid Chart

Generate a Mermaid JS chart representation:

```java
ExampleGraph exampleGraph = new ExampleGraph();
exampleGraph.init();
String chart = exampleGraph.toString();
System.out.println(chart);
```

## Running Tests

```bash
mvn test
```

## More Examples

See [GraphTest.java](./src/test/java/com/github/rizvn/graph/GraphTest.java) for more examples of graph usage.

## License

MIT License

