package csv

import java.io.{Reader, FileReader, File}
import au.com.bytecode.opencsv.CSVReader
import base.reader.ReaderLike
import core._

object CsvReaderCreator {

  def getCsvReader(file: File): CSVReader = new CSVReader(new FileReader(file))

  def getCsvReader(reader: Reader): CSVReader = new CSVReader(reader)

}
