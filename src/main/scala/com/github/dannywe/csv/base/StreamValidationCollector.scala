package com.github.dannywe.csv.base

import com.github.dannywe.csv.validation.LineConstraintViolation

import scalaz.stream.Process
import scalaz.concurrent.Task
import com.github.dannywe.csv.core.TypeAliases._
import javax.validation.ConstraintViolation
import javax.validation.{ConstraintViolation, Validation, Validator}
import scala.collection.JavaConversions._
import scala.util.{Failure, Try}


class StreamValidationCollector(validator: StreamValidator) {

  def validateAll[T](process: Process[Task, (Try[T], Int)]): EitherResult[T] = {
    val voSeq = process.runLog.run
    val validationSeq: Seq[LineConstraintViolation] = voSeq.map(validator.validate)

    validationSeq match {
      case x: Seq[LineConstraintViolation] if x.filter(_.hasError).isEmpty => Left(voSeq.map(_._1.get))
      case _ => Right(validationSeq)
    }
  }

  def validate[T](process: Process[Task, (Try[T], Int)], buffer: Int = 1): EitherResult[T] = {

    val rows: Process[Task, Either[(Try[T], Int), LineConstraintViolation]] = process
      .map(t => {
        val validationResult = validator.validate(t)
          if (validationResult.hasError) {
            Right(validationResult)
          } else {
            Left(t)
          }
        }).takeThrough()


    val result: Seq[Either[(Try[T], Int), LineConstraintViolation]] = rows.runLog.run

    val validation = result.filter(t => t.isRight).take(buffer)

    if(validate.nonEmpty) {
      Right(validate)
    }
    else {
      val voSeq = process.runLog.run
      Left(voSeq.map(_._1.get))
    }
  }

}
