package base.reader

import core._

trait ReaderLike {
  @throws[Exception] def readAll(): Seq[StringArray]
  @throws[Exception] def close(): Unit
}

