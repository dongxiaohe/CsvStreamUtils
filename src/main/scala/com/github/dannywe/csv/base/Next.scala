package com.github.dannywe.csv.base

trait Next[+T] {

 @throws[Exception] def get: T
 @throws[Exception] def shouldStop: Boolean

}
