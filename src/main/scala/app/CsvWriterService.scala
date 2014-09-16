package app

import java.io.Writer

import base.writer.{Iterator, WriterService}
import com.google.common.base.Function
import conversion.ConverterUtils._
import core._

class CsvWriterService extends WriterService{

  def writeTOStream[T](it: Iterator[T],f: Function[T, StringArray], writer: Writer): Unit = {
    write(it, f, writer)
  }


}
