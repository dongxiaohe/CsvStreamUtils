package com.xiaohedong.csv.core

import com.xiaohedong.csv.validation.LineConstraintViolation

import scala.util.Try
import scalaz.concurrent.Task
import scalaz.stream.Process

object TypeAliases {
  type StringArray = Array[String]

  type ProcessF[T] = Process[Task, (Try[T], Int)] => Process[Task, (Try[T], Int)]

  type EitherResult[T] = Either[Seq[T], Seq[LineConstraintViolation]]

}
