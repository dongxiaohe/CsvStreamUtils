package com.github.dannywe.csv.app

import java.io.{Reader, File}
import com.github.dannywe.csv.base.reader.{ReaderService, ReaderLike}
import com.github.dannywe.csv.core.TypeAliases._
import com.github.dannywe.csv.base._
import com.github.dannywe.csv.validation.JavaxValidator
import scalaz.stream.Process
import scalaz.concurrent.Task
import scala.util.Try

class CsvReaderService extends ReaderService with StreamTransformer {

  val validationCollector: StreamValidationCollector = new StreamValidationCollector(JavaxValidator)

  class ProcessModule extends ProcessModuleLike {
    override def validateAll[T](process: Process[Task, (Try[T], Int)]): EitherResult[T] = validationCollector.validateAll(process)
    override def transform[T](reader: ReaderLike, f: (StringArray) => T): Process[Task, (Try[T], Int)] = transformT(reader, f)
    override def validate[T](process: Process[Task, (Try[T], Int)], buffer: Int): EitherResult[T] = {
      require(buffer > 0, "the validation buffer should be more than 0")
      validationCollector.validate(process, buffer)
    }
  }

  class ReaderCreationModule extends ReaderCreationModuleLike {
    override def getReader(reader: Reader): ReaderLike = CsvReaderAdaptor(reader)
    override def getReader(file: File): ReaderLike = CsvReaderAdaptor(file)
  }

  override def readerCreationModule = new ReaderCreationModule
  override def processModule = new ProcessModule
}
