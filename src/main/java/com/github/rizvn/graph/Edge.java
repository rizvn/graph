package com.github.rizvn.graph;

import java.util.function.BooleanSupplier;

/**
 * Represents an edge in the graph that connects one node to another.
 * Edges can have conditions that determine whether the transition should occur.
 */
public class Edge {
    private String description;
    private String to;
    private BooleanSupplier condition;

    /**
     * Creates a new edge.
     *
     * @param to          The target node name
     * @param condition   The condition that must be true to follow this edge
     * @param description Optional description of the edge
     */
    public Edge(String to, BooleanSupplier condition, String description) {
        this.to = to;
        this.condition = condition;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public BooleanSupplier getCondition() {
        return condition;
    }

    public void setCondition(BooleanSupplier condition) {
        this.condition = condition;
    }
}

