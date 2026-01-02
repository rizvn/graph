package graph_test

import (
	"fmt"
	"testing"

	g "github.com/rizvn/graph"
)

// Create a mock graph struct for testing
type ExampleGraph struct {
	g.Graph
	counter int
}

// Define nodes and edges of the graph
func (r *ExampleGraph) Init() {

	// Initialize the graph
	r.InitGraph()

	// on start run Step1
	r.StepNamed(g.START, r.Step1).
		// then goto step 2
		When(g.DEFAULT, r.Step2, "Proceed to Step 2")

	// define Step2
	r.Step(r.Step2).
		// after step 2, if Step2RepeatCondition is true goto Step2
		When(r.Step2RepeatCondition, r.Step2, "").

		// after step 2, if Step2ContinueCondition is true goto Step3
		When(r.Step2ContinueCondition, r.Step3, "Repeat step 3 until counter is  3")

	// define Step3
	r.Step(r.Step3)

}

// Step1 logic
func (r *ExampleGraph) Step1() {
	fmt.Println("Counter initialised to 0")
	r.counter = 0
}

// Step2 logic
func (r *ExampleGraph) Step2() {
	fmt.Println("Increment counter")
	r.counter++
	fmt.Printf("Counter: %d\n", r.counter)
}

// Condition to continue to from Step2
func (r *ExampleGraph) Step2ContinueCondition() bool {
	return r.counter >= 3
}

// Condition to repeat Step2
func (r *ExampleGraph) Step2RepeatCondition() bool {
	return r.counter < 3
}

// Step3 logic
func (r *ExampleGraph) Step3() {
	fmt.Println("Reached Step 3, Graph execution complete.")
}

// Test the graph execution
func TestGraph(t *testing.T) {

	t.Run("Run Graph", func(t *testing.T) {
		exampleGraph := &ExampleGraph{}
		exampleGraph.Init()
		exampleGraph.RunGraph()
	})

	t.Run("Run Graph with hooks", func(t *testing.T) {
		exampleGraph := &ExampleGraph{}
		exampleGraph.Init()

		exampleGraph.HookBeforeStep = func(stepName string) {
			fmt.Printf("Before executing step: %s\n", stepName)
		}

		exampleGraph.HookAfterStep = func(stepName string) {
			fmt.Printf("After executing step: %s\n", stepName)
		}

		exampleGraph.HookBeforeTransition = func(from string, to string, desc string) {
			fmt.Printf("Transitioning from %s to %s  Desc: %s \n", from, to, desc)
		}

		exampleGraph.RunGraph()
	})

	// Test overriding the starting step, this can be useful for resuming graphs
	// for long running processes, where you may persist data to db
	// then resume from a later step instead of starting from beginning
	t.Run("Override Start graph at a different step ", func(t *testing.T) {
		exampleGraph := &ExampleGraph{}
		exampleGraph.Init()

		//since we are skipping step 1, initialise counter here
		exampleGraph.counter = 0

		// Override starting point to Step2
		exampleGraph.SetStepToRun("Step2")

		exampleGraph.HookBeforeStep = func(stepName string) {
			fmt.Printf("Before executing step: %s\n", stepName)
		}

		exampleGraph.RunGraph()
	})

	t.Run("Visualise graphh as mermaid chart", func(t *testing.T) {
		exampleGraph := &ExampleGraph{}
		exampleGraph.Init()
		chart := exampleGraph.String()

		fmt.Printf("Mermaid Chart: \n%s", chart)
	})
}
