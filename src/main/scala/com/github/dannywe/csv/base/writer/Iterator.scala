package com.github.dannywe.csv.base.writer

import com.github.dannywe.csv.base.Next

trait Iterator[T] {


  @throws[Exception] def next: Next[T]
  @throws[Exception] def close(): Unit

}
