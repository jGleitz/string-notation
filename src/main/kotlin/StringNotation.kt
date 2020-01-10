package de.joshuagleitze.stringnotation

import java.lang.Character.isJavaIdentifierPart
import java.lang.Character.isJavaIdentifierStart
import java.util.*
import java.util.stream.IntStream

/**
 * A convention for representing [Words][Word].
 */
interface StringNotation {
	/**
	 * Transforms an input string that is in `this` notation into a notation-agnostic [Word].
	 */
	fun parse(sourceString: String): Word

	/**
	 * Transforms a [Word] into a string that is formatted according to `this` notation.
	 */
	fun print(word: Word): String
}

/**
 * Base class for implementing string notations.
 *
 * @constructor Creates a string notation that will use the provided regular expression to [split][String.split] parts when parsing.
 */
abstract class BaseStringNotation(private val splitAt: Regex): StringNotation {
	/**
	 * Transforms a parsed part after it has been read. The default implementation is to convert the part to lowercase to discard possibly
	 * wrong case information.
	 */
	protected open fun transformPartAfterParse(index: Int, part: String) = part.toLowerCase(Locale.ROOT)

	override fun parse(sourceString: String): Word = Word(sourceString.split(splitAt).asSequence().mapIndexed(::transformPartAfterParse))

	/**
	 * Allows to transform a part before it is being printed. The default implementation does not modify the part in any way.
	 */
	protected open fun transformPartToPrint(index: Int, part: String) = part

	/**
	 * Allows to print characters in front of parts. The default implementation will print nothing before the first part and delegate to
	 * [printBeforeInnerPart] for the remaining parts.
	 */
	protected open fun printBeforePart(index: Int, part: String) = if (index == 0) "" else printBeforeInnerPart(index, part)

	/**
	 * Allows to print characters in front of parts that are not the first part. The default implementation prints nothing.
	 */
	protected open fun printBeforeInnerPart(index: Int, part: String) = ""

	override fun print(word: Word) = word.parts
		.mapIndexed(::transformPartToPrint)
		.foldIndexed(StringBuffer()) { index, left, right -> left.append(printBeforePart(index, right)).append(right) }
		.toString()
}

internal fun String.capitalizeOnlyFirst() = if (isNotEmpty()) this[0].toUpperCase() + substring(1).toLowerCase() else this

private val camelCaseSplitRegex = Regex("(?<=.)(?=\\p{Lu})")

/**
 * The `UpperCamelCase` notation.
 *
 * @see JavaTypeName
 */
object UpperCamelCase: BaseStringNotation(camelCaseSplitRegex) {
	public override fun transformPartToPrint(index: Int, part: String) = part.capitalizeOnlyFirst()
}

/**
 * The `lowerCamelCase` notation.
 *
 * @see JavaMemberName
 */
object LowerCamelCase: BaseStringNotation(camelCaseSplitRegex) {
	override fun transformPartToPrint(index: Int, part: String) = if (index == 0) part.toLowerCase() else part.capitalizeOnlyFirst()
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
	override fun transformPartToPrint(index: Int, part: String) = part.keepOnlyJavaIdentifierChars().capitalizeOnlyFirst()
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
			val rightPart = right.keepOnlyJavaIdentifierChars().run {
				if (left.contains(Regex("[a-zA-Z]"))) capitalizeOnlyFirst()
				else this.toLowerCase()
			}
			left.append(printBeforePart(index, rightPart)).append(rightPart)
		}.toString()

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

internal fun String.keepOnlyJavaIdentifierChars() = this.chars()
	.skipWhile { !isJavaIdentifierStart(it) }
	.filter { isJavaIdentifierPart(it) }
	.collect({ StringBuilder() }, { left, right -> left.appendCodePoint(right) }, { left, right -> left.append(right) })
	.toString()

internal fun IntStream.skipWhile(condition: (Int) -> Boolean): IntStream {
	var found = false
	return this.filter {
		if (!found) {
			found = !condition(it)
		}
		found
	}
}
