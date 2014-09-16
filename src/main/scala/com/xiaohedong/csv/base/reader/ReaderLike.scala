package com.xiaohedong.csv.base.reader

import com.xiaohedong.csv.core.TypeAliases._

trait ReaderLike {
  @throws[Exception] def readAll(): Seq[StringArray]
  @throws[Exception] def close(): Unit
}

