package de.joshuagleitze.stringnotation

import java.util.*

internal val camelCaseSplitRegex = Regex("(?<=.)(?=\\p{Lu})")

/**
 * The `UpperCamelCase` notation.
 *
 * @see JavaTypeName
 */
object UpperCamelCase: BaseStringNotation(camelCaseSplitRegex) {
	override fun transformPartAfterParse(index: Int, part: String) = part.toLowerCase(Locale.ROOT)

	public override fun transformPartToPrint(index: Int, part: String) = part.toFirstUpperOtherLowerCase()
}

/**
 * The `lowerCamelCase` notation.
 *
 * @see JavaMemberName
 */
object LowerCamelCase: BaseStringNotation(camelCaseSplitRegex) {
	override fun transformPartAfterParse(index: Int, part: String) = part.toLowerCase(Locale.ROOT)

	override fun transformPartToPrint(index: Int, part: String) = if (index == 0) part.toLowerCase() else part.toFirstUpperOtherLowerCase()
}

/**
 * The `SCREAMING_SNAKE_CASE` notation.
 */
object ScreamingSnakeCase: BaseStringNotation(Regex("_")) {
	override fun transformPartAfterParse(index: Int, part: String) = part.toLowerCase(Locale.ROOT)

	override fun printBeforeInnerPart(index: Int, part: String) = "_"

	override fun transformPartToPrint(index: Int, part: String) = part.toUpperCase()
}

/**
 * The `snake_case` notation.
 */
object SnakeCase: BaseStringNotation(Regex("_")) {
	override fun printBeforeInnerPart(index: Int, part: String) = "_"
}

/**
 * Notation for words written like in normal language. [Parsing][StringNotation.parse] will recognise all substrings that are separated by
 * one or more characters of whitespace as a [part][Word.parts]. [Printing][StringNotation.print] will print the parts separated by one
 * space.
 */
object NormalWords: BaseStringNotation(Regex("[\\s]+")) {
	override fun printBeforeInnerPart(index: Int, part: String) = " "
}

internal fun String.toFirstUpperOtherLowerCase() = if (isNotEmpty()) this[0].toUpperCase() + substring(1).toLowerCase() else this

