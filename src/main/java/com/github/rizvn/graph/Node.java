package com.github.rizvn.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * Represents a node in the graph.
 * Each node has a function to execute and edges to other nodes.
 */
public class Node {
    private Runnable func;
    private List<Edge> edges;

    /**
     * Creates a new node with the given function.
     *
     * @param func The function to execute when this node is visited
     */
    public Node(Runnable func) {
        this.func = func;
        this.edges = new ArrayList<>();
    }

    /**
     * Adds a conditional edge to another node by name.
     *
     * @param condition   The condition that must be true to follow this edge
     * @param to          The target node name
     * @param description Optional description of the edge
     * @return This node for method chaining
     */
    public Node whenNamed(BooleanSupplier condition, String to, String description) {
        edges.add(new Edge(to, condition, description));
        return this;
    }

    /**
     * Adds a conditional edge to another node by name with no description.
     *
     * @param condition The condition that must be true to follow this edge
     * @param to        The target node name
     * @return This node for method chaining
     */
    public Node whenNamed(BooleanSupplier condition, String to) {
        return whenNamed(condition, to, "");
    }

    public Runnable getFunc() {
        return func;
    }

    public void setFunc(Runnable func) {
        this.func = func;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }
}

