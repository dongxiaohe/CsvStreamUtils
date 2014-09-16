
import validation.LineConstraintViolation

import scala.util.Try
import scalaz.concurrent.Task
import scalaz.stream.Process

package object core {

  type StringArray = Array[String]

  type ProcessF[T] = Process[Task, (Try[T], Int)] => Process[Task, (Try[T], Int)]

  type EitherResult[T] = Either[Seq[T], Seq[LineConstraintViolation]]

}
