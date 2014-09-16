package com.xiaohedong.csv.builder

import com.google.common.base.Function
import com.xiaohedong.csv.conversion.ConverterUtils._
import com.xiaohedong.csv.core.TypeAliases._

import scala.util.{Failure, Success}

class StreamOperationBuilder[T] {

  def drop(x: Int): ProcessF[T] = { process =>
    process.drop(x)
  }

  def take(x: Int): ProcessF[T] = { process =>
    process.take(x)
  }

  def takeWhile(f: Function[T, Boolean]): ProcessF[T] = takeWhileF(f)

  def takeWhileF(f: T => Boolean): ProcessF[T] = { process =>
    process.takeWhile(t => {
      t._1 match {
        case Success(w) => f(w)
        case Failure(_) => false
      }
    })
  }

}
