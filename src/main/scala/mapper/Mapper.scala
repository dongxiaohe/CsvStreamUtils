package mapper

trait Mapper[T, W] {
  def mapTo(x: T): W
}
