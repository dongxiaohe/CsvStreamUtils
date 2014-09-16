package com.xiaohedong.csv.base

import com.xiaohedong.csv.validation.LineConstraintViolation

import scalaz.stream.Process
import scalaz.concurrent.Task
import com.xiaohedong.csv.core.TypeAliases._
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

    val errorRows: Process[Task, LineConstraintViolation] = process
      .map(validator.validate)
      .filter(_.hasError)
      .take(buffer)

    val validate: Seq[LineConstraintViolation] = errorRows.runLog.run

    if(validate.nonEmpty) {
      Right(validate)
    }
    else {
      val voSeq = process.runLog.run
      Left(voSeq.map(_._1.get))
    }
  }

}
