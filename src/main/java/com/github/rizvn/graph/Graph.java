package com.github.rizvn.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

/**
 * A graph-based programming library for Java.
 * This class allows you to define a directed graph of nodes and edges with conditional transitions.
 */
public class Graph {
    public static final String START = "start";
    public static final BooleanSupplier DEFAULT = () -> true;

    private Map<String, Node> nodes;
    private String currentNode;

    // Hooks for before and after node and edge execution
    private StepHook hookBeforeStep;
    private StepHook hookAfterStep;
    private TransitionHook hookBeforeTransition;

    /**
     * Functional interface for step hooks.
     */
    @FunctionalInterface
    public interface StepHook {
        void execute(String stepName);
    }

    /**
     * Functional interface for transition hooks.
     */
    @FunctionalInterface
    public interface TransitionHook {
        void execute(String fromStep, String toStep, String description);
    }

    /**
     * Initializes the graph with an empty node map and sets the current node to START.
     */
    public void initGraph() {
        if (nodes == null) {
            nodes = new HashMap<>();
        }
        if (currentNode == null || currentNode.isEmpty()) {
            currentNode = START;
        }
    }

    /**
     * Override the next node in the graph.
     *
     * @param stepName The name of the step to run next
     */
    public void setStepToRun(String stepName) {
        this.currentNode = stepName;
    }

    /**
     * Adds a node to the graph with a specific name.
     *
     * @param name The name of the node
     * @param func The function to execute when this node is visited
     * @return The created node for method chaining
     */
    public Node stepNamed(String name, Runnable func) {
        Node node = new Node(func);
        nodes.put(name, node);
        return node;
    }

    /**
     * Adds a node to the graph with a name derived from the method reference.
     * Note: In Java, getting method names from lambdas is complex, so you should use stepNamed() instead
     * or pass the method name explicitly.
     *
     * @param name The name of the step
     * @param func The function to execute when this node is visited
     * @return The created node for method chaining
     */
    public Node step(String name, Runnable func) {

        return stepNamed(name, func);
    }

    /**
     * Executes the graph starting from the current node.
     * The graph will continue executing until there are no more valid transitions.
     */
    public void runGraph() {
        while (true) {
            Node node = nodes.get(currentNode);
            if (node == null) {
                break;
            }

            // Call hook before step
            if (hookBeforeStep != null) {
                hookBeforeStep.execute(currentNode);
            }

            // Execute the node function
            node.getFunc().run();

            // Call hook after step
            if (hookAfterStep != null) {
                hookAfterStep.execute(currentNode);
            }

            // Find the next node based on edge conditions
            String nextId = "";
            for (Edge edge : node.getEdges()) {
                if (edge.getCondition().getAsBoolean()) {
                    nextId = edge.getTo();
                    // Call hook before transition
                    if (hookBeforeTransition != null) {
                        String from = currentNode;
                        String to = nextId;
                        String description = edge.getDescription();
                        hookBeforeTransition.execute(from, to, description);
                    }
                    break;
                }
            }

            if (nextId.isEmpty()) {
                break;
            }

            currentNode = nextId;
        }
    }

    /**
     * Generates a Mermaid JS graph string representation of the graph.
     *
     * @return A Mermaid JS formatted string
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("graph TD\n");

        for (Map.Entry<String, Node> entry : nodes.entrySet()) {
            String nodeId = entry.getKey();
            Node node = entry.getValue();

            for (Edge edge : node.getEdges()) {
                if (edge.getDescription() != null && !edge.getDescription().isEmpty()) {
                    sb.append(nodeId).append(" -- \"").append(edge.getDescription()).append("\" --> ")
                            .append(edge.getTo()).append("\n");
                } else {
                    sb.append(nodeId).append(" --> ").append(edge.getTo()).append("\n");
                }
            }
        }

        return sb.toString();
    }

    // Getters and setters

    public Map<String, Node> getNodes() {
        return nodes;
    }

    public void setNodes(Map<String, Node> nodes) {
        this.nodes = nodes;
    }

    public String getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(String currentNode) {
        this.currentNode = currentNode;
    }

    public StepHook getHookBeforeStep() {
        return hookBeforeStep;
    }

    public void setHookBeforeStep(StepHook hookBeforeStep) {
        this.hookBeforeStep = hookBeforeStep;
    }

    public StepHook getHookAfterStep() {
        return hookAfterStep;
    }

    public void setHookAfterStep(StepHook hookAfterStep) {
        this.hookAfterStep = hookAfterStep;
    }

    public TransitionHook getHookBeforeTransition() {
        return hookBeforeTransition;
    }

    public void setHookBeforeTransition(TransitionHook hookBeforeTransition) {
        this.hookBeforeTransition = hookBeforeTransition;
    }
}

