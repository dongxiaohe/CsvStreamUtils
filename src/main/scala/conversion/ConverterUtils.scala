package conversion

import com.google.common.base.Function

object ConverterUtils {

  implicit def convertStringArrayToT[T](f: Function[Array[String], T]): Array[String] => T = {
    val fFromScala: Array[String] => T = { array => f.apply(array)}
    fFromScala
  }

  implicit def convertTToStringArray[T](f: Function[T, Array[String]]): T => Array[String] = {
    val fFromScala: T => Array[String] = { t => f.apply(t)}
    fFromScala
  }

  implicit def convertTToBoolean[T](f: Function[T, Boolean]): T => Boolean = {
    val fFromScala: T => Boolean = { t => f.apply(t)}
    fFromScala
  }

}
