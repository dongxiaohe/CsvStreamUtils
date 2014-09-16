package csv

import base.reader.ReaderLike
import core.StringArray
import au.com.bytecode.opencsv.CSVReader
import scala.collection.JavaConversions._
import java.io.{Reader, File}

class CsvReaderAdaptor(csvReader: CSVReader) extends ReaderLike {

  override def close(): Unit = csvReader.close()

  override def readAll(): Seq[StringArray] = csvReader.readAll()

}

object CsvReaderAdaptor {

  def apply(file: File) = new CsvReaderAdaptor(CsvReaderCreator.getCsvReader(file))
  def apply(reader: Reader) = new CsvReaderAdaptor(CsvReaderCreator.getCsvReader(reader))


}
