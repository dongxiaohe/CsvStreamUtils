package com.xiaohedong.csv.base.writer

class Stop[T] extends Next{

  override def shouldStop: Boolean = true

}
