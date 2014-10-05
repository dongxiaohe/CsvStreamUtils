package com.github.dannywe.csv.base.reader

import java.io.{File, Reader}

import com.github.dannywe.csv.core.TypeAliases._
import com.github.dannywe.csv.vo.Result

import scala.util.Try
import scalaz.concurrent.Task
import scalaz.stream.Process

trait ReaderService {

  def readerCreationModule: ReaderCreationModule
  def processModule: ProcessModule

  type ReaderCreationModule <: ReaderCreationModuleLike
  
  trait ReaderCreationModuleLike { this: ReaderCreationModule =>
    def getReader(file: File): ReaderLike
    def getReader(reader: Reader): ReaderLike
  }
  
  type ProcessModule <: ProcessModuleLike

  trait ProcessModuleLike { this: ProcessModule =>
    def validate[T](process: Process[Task, (Try[T], Int)], buffer: Int): EitherResult[T]
    def transform[T](reader: ReaderLike, f: StringArray => T): Process[Task, (Try[T], Int)]
    def validateAll[T](process: Process[Task, (Try[T], Int)]): EitherResult[T]
  }

  def parse[T](file: File, f: StringArray => T): Result[T] = {
    val readerLike: ReaderLike = readerCreationModule.getReader(file)
    val processedResult: EitherResult[T] = processModule.validateAll(processModule.transform(readerLike, f))
    readerLike.close()
    Result(processedResult)
  }

  def parse[T](reader: Reader, f: StringArray => T): Result[T] = {
    val readerLike: ReaderLike = readerCreationModule.getReader(reader)
    val processedResult: EitherResult[T] = processModule.validateAll(processModule.transform(readerLike, f))
    readerLike.close()
    Result(processedResult)
  }

  def parse[T](file: File, f: StringArray => T, ops: ProcessF[T] ): Result[T] = {
    val readerLike: ReaderLike = readerCreationModule.getReader(file)
    val operatedProcess: Process[Task, (Try[T], Int)] = ops(processModule.transform(readerLike, f))
    val processedResult: EitherResult[T] = processModule.validateAll(operatedProcess)
    readerLike.close()
    Result(processedResult)
  }

  def parse[T](reader: Reader, f: StringArray => T, ops: ProcessF[T] ): Result[T] = {
    val readerLike: ReaderLike = readerCreationModule.getReader(reader)
    val operatedProcess: Process[Task, (Try[T], Int)] = ops(processModule.transform(readerLike, f))
    val processedResult: EitherResult[T] = processModule.validateAll(operatedProcess)
    readerLike.close()
    Result(processedResult)
  }

  def parse[T](file: File, f: StringArray => T, buffer: Int): Result[T] = {
    val readerLike: ReaderLike = readerCreationModule.getReader(file)
    val operatedProcess: Process[Task, (Try[T], Int)] = processModule.transform(readerLike, f)
    val eitherResult = processModule.validate(operatedProcess, buffer)
    readerLike.close()
    Result(eitherResult)
  }

  def parse[T](reader: Reader, f: StringArray => T, buffer: Int): Result[T] = {
    val readerLike: ReaderLike = readerCreationModule.getReader(reader)
    val operatedProcess: Process[Task, (Try[T], Int)] = processModule.transform(readerLike, f)
    val eitherResult = processModule.validate(operatedProcess, buffer)
    readerLike.close()
    Result(eitherResult)
  }

  def parse[T](file: File, f: StringArray => T, ops: ProcessF[T], buffer: Int): Result[T] = {
    val readerLike: ReaderLike = readerCreationModule.getReader(file)
    val operatedProcess: Process[Task, (Try[T], Int)] = ops(processModule.transform(readerLike, f))
    val eitherResult = processModule.validate(operatedProcess, buffer)
    readerLike.close()
    Result(eitherResult)
  }

  def parse[T](reader: Reader, f: StringArray => T, ops: ProcessF[T], buffer: Int): Result[T] = {
    val readerLike: ReaderLike = readerCreationModule.getReader(reader)
    val operatedProcess: Process[Task, (Try[T], Int)] = ops(processModule.transform(readerLike, f))
    val eitherResult = processModule.validate(operatedProcess, buffer)
    readerLike.close()
    Result(eitherResult)
  }
}
