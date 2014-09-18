package com.github.dannywe.csv.base.writer

import au.com.bytecode.opencsv.CSVWriter
import com.github.dannywe.csv.base.Next
import com.github.dannywe.csv.core.TypeAliases._

import scalaz.concurrent.Task
import scalaz.stream._

trait WriterService {

  def write[T](it: Iterator[T], f: T => StringArray, writer: java.io.Writer): Unit = {
    val csvWriter: CSVWriter = new CSVWriter(writer)
    
    def mapToT: Next[T] => Task[StringArray] = t => Task(f(t.get))

    val result: Process[Task, Next[T]] = Process.repeatEval(Task{it.next}).takeWhile(t => !t.shouldStop)

    val channel = Process.constant(mapToT)

    (result through channel).map(t => csvWriter.writeNext(t)).run.run

    csvWriter.close()

  }

}

