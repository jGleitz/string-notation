package de.joshuagleitze.stringnotation

import java.util.stream.IntStream

private val camelCaseSplitRegex = Regex("(?<=.)(?=\\p{Lu})")

/**
 * The `UpperCamelCase` notation.
 *
 * @see JavaTypeName
 */
object UpperCamelCase: BaseStringNotation(camelCaseSplitRegex) {
	public override fun transformPartToPrint(index: Int, part: String) = part.toFirstUpperOtherLowerCase()
}

/**
 * The `lowerCamelCase` notation.
 *
 * @see JavaMemberName
 */
object LowerCamelCase: BaseStringNotation(camelCaseSplitRegex) {
	override fun transformPartToPrint(index: Int, part: String) = if (index == 0) part.toLowerCase() else part.toFirstUpperOtherLowerCase()
}

/**
 * The `SCREAMING_SNAKE_CASE` notation.
 */
object ScreamingSnakeCase: BaseStringNotation(Regex("_")) {
	override fun printBeforeInnerPart(index: Int, part: String) = "_"

	override fun transformPartToPrint(index: Int, part: String) = part.toUpperCase()
}

/**
 * The `snake_case` notation.
 */
object SnakeCase: BaseStringNotation(Regex("_")) {
	override fun transformPartAfterParse(index: Int, part: String) = part
	override fun printBeforeInnerPart(index: Int, part: String) = "_"
}

/**
 * A notation for java type names. This notation is like [UpperCamelCase], but will drop any character that is not allowed in a Java
 * identifier when [printing][StringNotation.print].
 *
 * Allowed characters are determined using [Character.isJavaIdentifierStart] and [Character.isJavaIdentifierPart].
 */
object JavaTypeName: BaseStringNotation(camelCaseSplitRegex) {
	override fun transformPartToPrint(index: Int, part: String) = part.toFirstUpperOtherLowerCase()
	override fun print(word: Word) = super.print(word).keepOnlyJavaIdentifierChars()
}

/**
 * A notation for java member names. This notation is like [LowerCamelCase], but will drop any character that is not allowed in a Java
 * identifier when [printing][StringNotation.print].
 *
 * Allowed characters are determined using [Character.isJavaIdentifierStart] and [Character.isJavaIdentifierPart].
 */
object JavaMemberName: BaseStringNotation(camelCaseSplitRegex) {
	override fun print(word: Word) = word.parts
		.foldIndexed(StringBuffer()) { index, left, right ->
			val rightPart =
				if (left.contains(Regex("[a-zA-Z]"))) right.toFirstUpperOtherLowerCase()
				else right.toLowerCase()
			left.append(printBeforePart(index, rightPart)).append(rightPart)
		}.toString().keepOnlyJavaIdentifierChars()

}

/**
 * Notation for words written like in normal language. [Parsing][StringNotation.parse] will recognise all substrings that are separated by
 * one or more characters of whitespace as a [part][Word.parts]. [Printing][StringNotation.print] will print the parts separated by one
 * space.
 */
object NormalWords: BaseStringNotation(Regex("[\\s]+")) {
	override fun transformPartAfterParse(index: Int, part: String) = part
	override fun printBeforeInnerPart(index: Int, part: String) = " "
}

internal fun String.toFirstUpperOtherLowerCase() = if (isNotEmpty()) this[0].toUpperCase() + substring(1).toLowerCase() else this

fun String.keepOnlyJavaIdentifierChars() = this.chars()
	.skipWhile { !Character.isJavaIdentifierStart(it) }
	.filter { Character.isJavaIdentifierPart(it) }
	.collect({ StringBuilder() }, { left, right -> left.appendCodePoint(right) }, { left, right -> left.append(right) })
	.toString()

internal inline fun IntStream.skipWhile(crossinline condition: (Int) -> Boolean): IntStream {
	var found = false
	return this.filter {
		if (!found) {
			found = !condition(it)
		}
		found
	}
}

