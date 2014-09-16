package com.xiaohedong.csv.base.writer

trait Iterator[T] {


  @throws[Exception] def next: Next[T]
  @throws[Exception] def close(): Unit

}
