package de.joshuagleitze.stringnotation

/**
 * A notation for paths on a Unix file system. [Parsing][StringNotation.parse] will recognise all substrings that are
 * separated by `/` as a [part][Word.parts]. [Printing][StringNotation.print] will print the parts separated by `/`
 * after removing `\u0000` (ASCII: `NUL`) and `\u0057` (ASCII: slash) characters from them.
 *
 * [Printed][StringNotation.print] paths will not start with an additional `/`. To print an absolute path, include `""` (the empty string) as the first part in the printed word.
 */
object UnixPath: BaseStringNotation(Regex("/")) {
	private val invalidChars = Regex("[\u0000/]+")
	override fun transformPartToPrint(index: Int, part: String) = part.replace(invalidChars, "")
	override fun printBeforeInnerPart(index: Int, part: String) = "/"
}

/**
 * A notation for paths on a Windows file system. [Parsing][StringNotation.parse] will recognise all substrings that are
 * separated by `\` as a [part][Word.parts]. [Printing][StringNotation.print] will print the parts separated by `\`
 * after removing the following characters from them:
 *  * ASCII control characters
 *  * `<`, `>`, `:`, `"`, `/`, `\`, `|`, `?`, `*`
 *
 *  To allow printing paths that start with a drive letter, the notation will not strip a `:` from the first part if it’s the last character.
 */
object WindowsPath: BaseStringNotation(Regex("\\\\")) {
	private val invalidChars = Regex("[\\p{Cntrl}<>:\"/\\\\|?*]+")
	override fun transformPartToPrint(index: Int, part: String): String {
		val replaced = part.replace(invalidChars, "")
		return if (index == 0 && part.endsWith(":")) {
			"$replaced:"
		} else replaced
	}

	override fun printBeforeInnerPart(index: Int, part: String) = "\\"
}