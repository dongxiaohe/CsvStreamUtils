package com.xiaohedong.csv.vo

import java.util.{List => JList}

import com.xiaohedong.csv.core.TypeAliases._
import com.xiaohedong.csv.format._
import com.xiaohedong.csv.mapper.{Mapper, DefaultFieldMapper}
import com.xiaohedong.csv.validation.LineErrorConverter

import scala.collection.JavaConversions._


case class Result[T](either: EitherResult[T]) {

 lazy val isSuccessful: Boolean = either.isLeft
 lazy val isFailed: Boolean = either.isRight
 lazy val getResult: JList[T] = either.left.get
 lazy val getFailureResult: JList[LineErrorConverter] = {
   either.right.get.filter(t => t.hasError).map(LineErrorConverter).toList
  }

  def getSimplifiedFailureResult(mapper: Mapper[String, String]): JList[SimplifiedErrorContainer] = {
    val allErrors: Seq[SimplifiedErrorContainer] = either.right.get
      .filter(_.hasError)
      .map(LineErrorConverter(_)
      .getViolations(mapper))
      .flatten

    val partition: (Seq[SimplifiedErrorContainer], Seq[SimplifiedErrorContainer]) = allErrors.partition(_.columnName == null)
    (partition._2.sortBy(_.columnName) ++ partition._1).toList
  }

  def getSortedSimplifiedFailureResult(mapper: Mapper[String, SortedColumn]): JList[SimplifiedErrorContainer] = {
    either.right.get
      .filter(_.hasError)
      .map(LineErrorConverter(_)
      .getSortedViolations(mapper))
      .flatten
      .toList
  }

  def getFormattedErrorMessage(formatter: ErrorLineFormatter = DefaultErrorLineFormatter, split: String = "\n"): String = {
    val result: Seq[SimplifiedErrorContainer] = getSimplifiedFailureResult(DefaultFieldMapper)
    result.sortBy(_.lineNumber).map(t => formatter.format(t.lineNumber, t.columnName, t.errorMessage)).mkString(split)
  }

  def getSortedFormattedErrorMessage(mapper: Mapper[String, SortedColumn], formatter: ErrorLineFormatter = DefaultErrorLineFormatter, split: String = "\n"): String = {
    val result: Seq[SimplifiedErrorContainer] = getSortedSimplifiedFailureResult(mapper)
    result.sortBy(_.lineNumber).map(t => formatter.format(t.lineNumber, t.columnName, t.errorMessage)).mkString(split)
  }

  //for java

  def getSimplifiedFailureResult: JList[SimplifiedErrorContainer] = {
    getSimplifiedFailureResult(DefaultFieldMapper)
  }

  def getFormattedErrorMessage: String = {
    getFormattedErrorMessage(DefaultErrorLineFormatter, "\n")
  }

  def getSortedFormattedErrorMessage(mapper: Mapper[String, SortedColumn], split: String): String = {
    getSortedFormattedErrorMessage(mapper, DefaultErrorLineFormatter, split)
  }

  def getSortedFormattedErrorMessage(mapper: Mapper[String, SortedColumn]): String = {
    getSortedFormattedErrorMessage(mapper, DefaultErrorLineFormatter, "\n")
  }
}
