package com.github.dannywe.csv.mapper

trait Mapper[T, W] {
  def mapTo(x: T): W
}
