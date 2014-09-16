package format

object DefaultErrorLineFormatter extends ErrorLineFormatter {
  override def format(lineNumber: Int, columnName: String, errorMessage: String): String = {
    if (columnName == null) s"Line $lineNumber $errorMessage."
    else s"Line $lineNumber Column $columnName $errorMessage."
  }
}
