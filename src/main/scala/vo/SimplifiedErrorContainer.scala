package vo

case class SimplifiedErrorContainer(lineNumber: Int, columnName: String, errorMessage: String) {

  def this(lineNumber: Int, errorMessage: String) = {
    this(lineNumber, null, errorMessage)
  }

}
