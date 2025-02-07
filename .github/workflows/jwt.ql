import javascript

/**
 * @id custom-jwt-detection
 * @name Detect JWT Usage
 * @description This query detects the usage of JSON Web Tokens (JWT) in JavaScript code.
 * @kind problem
 * @tags security
 */

predicate isJWTLibrary(Expr e) {
  e.getType().hasQualifiedName("jsonwebtoken", "sign") or
  e.getType().hasQualifiedName("jsonwebtoken", "verify")
}

from CallExpr call
where isJWTLibrary(call.getCallee())
select call, "This code uses JSON Web Tokens (JWT)."
