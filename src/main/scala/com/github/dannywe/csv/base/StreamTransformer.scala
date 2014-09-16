package com.github.dannywe.csv.base

import com.github.dannywe.csv.base.reader.ReaderLike
import com.github.dannywe.csv.core.TypeAliases._
import scalaz.stream.Process
import scalaz.concurrent.Task
import scala.util.Try

trait StreamTransformer {

  def transformT[T](reader: ReaderLike, f: StringArray => T): Process[Task, (Try[T], Int)] = {
    def mapToT: StringArray => Task[Try[T]] = t => Task(Try{f(t)})

    val result: Process[Task, StringArray] = Process.emitAll(reader.readAll())

    val channel = Process.constant(mapToT)

    (result through channel).zipWithIndex.map(t => (t._1, t._2 + 1))
  }

}
