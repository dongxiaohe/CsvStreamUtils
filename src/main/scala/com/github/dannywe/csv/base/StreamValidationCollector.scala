package com.github.dannywe.csv.base

import com.github.dannywe.csv.validation.LineConstraintViolation

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
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

//    val result = process.scan(Seq[T]() -> Seq[LineConstraintViolation]()) { (r, e) =>
//      val (s, f) = r
//      val validationResult = validator.validate(e)
//      if (validationResult.hasError) s -> (f :+ validationResult)
//      else (s :+ e._1.get) -> f
//    }.takeWhile()
//      .dropWhile{case (succ, fail) => fail.size < buffer}
//      .take(1)
//
//    val run = result.runLast.run.get
//    if (run._2.isEmpty) Left(run._1)
//    else Right(run._2)

  }

}
