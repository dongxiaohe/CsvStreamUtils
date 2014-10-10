package com.github.dannywe.csv.base

import com.github.dannywe.csv.core.TypeAliases._
import com.github.dannywe.csv.validation.LineConstraintViolation

import scala.collection.mutable.ListBuffer
import scala.util.Try
import scalaz.concurrent.Task
import scalaz.stream.Process


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

    val accumulator = new ListBuffer[LineConstraintViolation]
    val taskProcess = process.map(t => {
      val validationResult = validator.validate(t)
      if (validationResult.hasError) {
        accumulator += validationResult
      }
      t
    }).takeWhile(_ => !(accumulator.size == buffer))

    val voSeq = taskProcess.runLog.run

    if (accumulator.isEmpty) Left(voSeq.map(_._1.get))
    else Right(accumulator)

  }

}
