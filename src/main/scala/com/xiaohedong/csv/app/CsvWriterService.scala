package com.xiaohedong.csv.app

import java.io.Writer

import com.xiaohedong.csv.base.writer.{Iterator, WriterService}
import com.google.common.base.Function
import com.xiaohedong.csv.conversion.ConverterUtils._
import com.xiaohedong.csv.core.TypeAliases._

class CsvWriterService extends WriterService{

  def writeTOStream[T](it: Iterator[T],f: Function[T, StringArray], writer: Writer): Unit = {
    write(it, f, writer)
  }


}
