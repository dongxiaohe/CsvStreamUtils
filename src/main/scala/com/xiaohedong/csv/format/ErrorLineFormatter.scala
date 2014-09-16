package com.xiaohedong.csv.format

trait ErrorLineFormatter {

  def format(lineNumber: Int, columnName: String, errorMessage: String): String

}
