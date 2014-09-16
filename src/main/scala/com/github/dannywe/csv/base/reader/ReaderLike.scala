package com.github.dannywe.csv.base.reader

import com.github.dannywe.csv.core.TypeAliases._

trait ReaderLike {
  @throws[Exception] def readAll(): Seq[StringArray]
  @throws[Exception] def close(): Unit
}

