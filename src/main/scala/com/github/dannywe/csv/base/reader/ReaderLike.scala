package com.github.dannywe.csv.base.reader

import com.github.dannywe.csv.base.Next
import com.github.dannywe.csv.core.TypeAliases._

trait ReaderLike {
  @throws[Exception] def readLine(): Next[StringArray]
  @throws[Exception] def close(): Unit
}

