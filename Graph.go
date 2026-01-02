package graph

import (
	"reflect"
	"runtime"
	"strings"
)

type Edge struct {
	Description string
	To          string
	Condition   func() bool
}

type Node struct {
	Func  func()
	Edges []*Edge
}

type Graph struct {
	Nodes       map[string]*Node
	CurrentNode string

	// Hooks for before and after node and edge execution
	// These can be set to custom functions for logging or other purposes
	HookBeforeStep       func(stepName string)
	HookAfterStep        func(stepName string)
	HookBeforeTransition func(fromStep string, toStep string, description string)
}

// SetStepToRun Override the next node in the graph
func (r *Graph) SetStepToRun(stepName string) {
	r.CurrentNode = stepName
}

// WhenNamed adds a conditional edge to the node
func (r *Node) WhenNamed(condition func() bool, to string, descriptions ...string) *Node {
	if r.Edges == nil {
		r.Edges = make([]*Edge, 0)
	}

	description := ""

	if len(descriptions) > 0 {
		description = descriptions[0]
	}

	r.Edges = append(r.Edges, &Edge{
		Description: description,
		To:          to,
		Condition:   condition,
	})
	return r
}

// When adds a conditional edge to the node name is derived from the function name
func (r *Node) When(condition func() bool, fn func(), descriptions ...string) *Node {
	name := GetMethodName(fn)
	return r.WhenNamed(condition, name, descriptions...)
}

// StepNamed adds a node to the graph with a specific name
func (r *Graph) StepNamed(name string, fn func()) *Node {
	n := Node{
		Func: fn,
	}

	r.Nodes[name] = &n
	return &n
}

// Step adds a node to the graph, name is derived from the function name
func (r *Graph) Step(fn func()) *Node {
	name := GetMethodName(fn)
	return r.StepNamed(name, fn)
}

var DEFAULT = func() bool {
	return true
}

const START = "start"

// InitGraph initializes the graph
func (r *Graph) InitGraph() {
	if r.Nodes == nil {
		r.Nodes = make(map[string]*Node)
	}

	if r.CurrentNode == "" {
		r.CurrentNode = START
	}
}

// RunGraph executes the graph starting from the current node
func (r *Graph) RunGraph() {
	for {
		node, ok := r.Nodes[r.CurrentNode]
		if !ok {
			break
		}

		// call hook
		if r.HookBeforeStep != nil {
			r.HookBeforeStep(r.CurrentNode)
		}
		// execute the node function
		node.Func()

		// call hook
		if r.HookAfterStep != nil {
			r.HookAfterStep(r.CurrentNode)
		}

		nextID := ""
		for _, edge := range node.Edges {
			if edge.Condition() {
				nextID = edge.To
				// call hook
				if r.HookBeforeTransition != nil {
					from := r.CurrentNode
					to := nextID
					description := edge.Description
					r.HookBeforeTransition(from, to, description)
				}
				break
			}
		}
		if nextID == "" {
			break
		}

		r.CurrentNode = nextID
	}
}

// String traverses all nodes and generates a Mermaid JS graph string
func (r *Graph) String() string {
	var sb strings.Builder
	sb.WriteString("graph TD\n")
	visited := make(map[string]bool)
	for nodeID, node := range r.Nodes {
		visited[nodeID] = true
		for _, edge := range node.Edges {
			if edge.Description != "" {
				sb.WriteString(nodeID + " -- \"" + edge.Description + "\" --> " + edge.To + "\n")
			} else {
				sb.WriteString(nodeID + " --> " + edge.To + "\n")
			}
		}
	}
	return sb.String()
}

// GetMethodName returns the name of the function, stripping any suffixes
// this is used to derive node names from function names
func GetMethodName(f any) string {
	val := reflect.ValueOf(f)
	pc := val.Pointer()
	funcObj := runtime.FuncForPC(pc)
	fqn := funcObj.Name()
	parts := strings.Split(fqn, ".")

	fn := parts[len(parts)-1]
	fn = strings.TrimSuffix(fn, "-fm")
	return fn
}
