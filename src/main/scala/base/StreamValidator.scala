package base

import javax.validation.Validator

import validation.{ColumnConstraintViolation, LineConstraintViolation, MissingColumnViolation}

import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}

class StreamValidator(validator: Validator) {
  def validate[T](line: (Try[T], Int)): LineConstraintViolation = {
    line._1 match {
      case Failure(t: java.lang.ArrayIndexOutOfBoundsException) =>
        MissingColumnViolation(line._2)
      case Success(t) =>
        ColumnConstraintViolation[T](line._2, validator.validate(t).toSet)
    }
  }
}
