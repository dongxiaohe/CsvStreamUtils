package com.github.dannywe.csv.app

import java.io.Writer

import com.github.dannywe.csv.base.writer.{Iterator, WriterService}
import com.google.common.base.Function
import com.github.dannywe.csv.conversion.ConverterUtils._
import com.github.dannywe.csv.core.TypeAliases._

class CsvWriterService extends WriterService{

  def writeTOStream[T](it: Iterator[T],f: Function[T, StringArray], writer: Writer): Unit = {
    write(it, f, writer)
  }


}
