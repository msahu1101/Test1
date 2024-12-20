import java
import semmle.code.java.dataflow.DataFlow
import semmle.code.java.dataflow.TaintTracking

/**
 * @name Input validation detection
 * @description Detects methods that take user input and checks if the input is validated
 * @kind problem
 * @problem.severity warning
 * @id java/input-validation
 */

class UserInput extends TaintTracking::Source {
  UserInput() { this.hasLocationInfo() and this.getType().getName() = "String" }
}

class ValidationMethod extends Method {
  ValidationMethod() { this.getName().matches("validate%") }
}

from UserInput input, Method method, DataFlow::PathNode source, DataFlow::PathNode sink
where
  input = source.getNode() and
  method = sink.getNode().getEnclosingCallable() and
  not exists(ValidationMethod v | v.getAParameter() = input)
select method, source, sink, "Potential missing input validation for user input."
