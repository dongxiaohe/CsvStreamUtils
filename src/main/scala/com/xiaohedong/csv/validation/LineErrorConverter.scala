package com.xiaohedong.csv.validation

import java.util.{List => JList}

import com.xiaohedong.csv.format.SortedColumn
import com.xiaohedong.csv.mapper.{DefaultFieldMapper, Mapper}
import com.xiaohedong.csv.vo.SimplifiedErrorContainer

import scala.collection.JavaConversions._

case class LineErrorConverter(errorLine: LineConstraintViolation) {

  val getLineNumber: Int = errorLine.getLineNumber

  def getViolations[T](mapper: Mapper[String, String]): JList[SimplifiedErrorContainer] = {
    errorLine match {
      case t: MissingColumnViolation => Seq(new SimplifiedErrorContainer(t.getLineNumber, t.getMessage))
      case t: ColumnConstraintViolation[T] => t.getConstraintViolationSet.map(w => SimplifiedErrorContainer(t.getLineNumber, mapper.mapTo(w.getPropertyPath.toString), w.getMessage)).toList
    }
  }


  def getSortedViolations[T](mapper: Mapper[String, SortedColumn]): JList[SimplifiedErrorContainer] = {
    errorLine match {
      case t: MissingColumnViolation => Seq(new SimplifiedErrorContainer(t.getLineNumber, t.getMessage))
      case t: ColumnConstraintViolation[T] => t.getConstraintViolationSet.toList
        .sortBy(w => mapper.mapTo(w.getPropertyPath.toString).columnNumber)
        .map(w => SimplifiedErrorContainer(t.getLineNumber, mapper.mapTo(w.getPropertyPath.toString).columnName, w.getMessage))
    }
  }

  def getViolations: JList[SimplifiedErrorContainer] = {
    getViolations(DefaultFieldMapper)
  }
}
