package com.github.dannywe.csv.base

import java.io.{File, FileReader, Reader}

import au.com.bytecode.opencsv.CSVReader

object CsvReaderCreator {

  def getCsvReader(file: File): CSVReader = new CSVReader(new FileReader(file))

  def getCsvReader(reader: Reader): CSVReader = new CSVReader(reader)

}
