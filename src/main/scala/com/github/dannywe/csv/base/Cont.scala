package com.github.dannywe.csv.base


case class Cont[T](t: T) extends Next[T] {
  @throws[Exception]
  override def get: T = t

  @throws[Exception]
  override def shouldStop: Boolean = false
}
