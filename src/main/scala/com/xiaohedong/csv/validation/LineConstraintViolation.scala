package com.xiaohedong.csv.validation

import javax.validation.ConstraintViolation

sealed trait LineConstraintViolation {
  def getLineNumber: Int
  def hasError: Boolean
}

case class MissingColumnViolation(lineNumber: Int) extends LineConstraintViolation {
  def getMessage: String = "may have missing columns"

  override def getLineNumber: Int = lineNumber

  override def hasError: Boolean = true
}

case class ColumnConstraintViolation[T](lineNumber: Int, constraintViolationSet: Set[ConstraintViolation[T]]) extends LineConstraintViolation {

  def getConstraintViolationSet: Set[ConstraintViolation[T]] = constraintViolationSet

  override def hasError: Boolean = constraintViolationSet != null && constraintViolationSet.nonEmpty

  override def getLineNumber: Int = lineNumber
}
