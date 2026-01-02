## Library for graph based programming in go

### Install
```bash
go get github.com/rizvn/graph
```

### Usage
```go
import (
	"fmt"
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

// Condition to continue from Step2
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
```

To run the graph:
```go
exampleGraph := &ExampleGraph{}
exampleGraph.Init()
exampleGraph.RunGraph()	
```

### More examples
see [Graph_test.go](./Graph_test.go) for more examples of graph usage.