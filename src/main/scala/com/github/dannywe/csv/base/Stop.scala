package com.github.dannywe.csv.base

case class Stop[T]() extends Next[T]{

  override def shouldStop: Boolean = true

  @throws[Exception]
  override def get: T = throw new Exception("cannot fetch data if it is stop state")
}
