import javascript

/**
 * @name SQL injection detection
 * @description Detects SQL queries constructed using string concatenation
 * @kind problem
 * @problem.severity error
 * @id js/sql-injection
 */

class SqlQuery extends Expr {
  SqlQuery() {
    this.getType().getName() = "string" and
    this.getAnAccess().getEnclosingCallable().getName() = "query"
  }
}

from SqlQuery query, Expr e
where
  e = query.getAnArgument() and
  e instanceof BinaryExpr and
  e.getOperator() = "+"
select query, e, "Potential SQL injection vulnerability: query constructed using string concatenation."
