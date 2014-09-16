package com.xiaohedong.csv.mapper

import com.google.common.base.CaseFormat

import scala.util.{Success, Try}

object DefaultFieldMapper extends Mapper[String, String] {
  override def mapTo(field: String): String = {
    val convertedField = Try {
      val lowercaseHeader = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, field).replaceAll("-", " ")
      lowercaseHeader.substring(0, 1).toUpperCase + lowercaseHeader.substring(1)
    }
    convertedField match {
      case Success(t) => t
      case _ => throw new Exception("value object field failed to be converted.")
    }
  }
}
