package format

trait ErrorLineFormatter {

  def format(lineNumber: Int, columnName: String, errorMessage: String): String

}
