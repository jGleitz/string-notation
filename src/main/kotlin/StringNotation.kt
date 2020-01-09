package de.joshuagleitze.stringnotation

import java.lang.Character.isJavaIdentifierPart
import java.lang.Character.isJavaIdentifierStart
import java.util.*
import java.util.stream.IntStream

interface StringNotation {
	fun parse(sourceString: String): Word
	fun print(word: Word): String
}

abstract class BaseStringNotation(private val splitAt: Regex): StringNotation {
	protected open fun transformPartAfterParse(index: Int, part: String) = part.toLowerCase(Locale.ROOT)

	override fun parse(sourceString: String): Word = Word(sourceString.split(splitAt).asSequence().mapIndexed(::transformPartAfterParse))

	protected open fun transformPartToPrint(index: Int, part: String) = part
	protected open fun printBeforePart(index: Int, part: String) = if (index == 0) "" else printBeforeInnerPart(index, part)

	protected open fun printBeforeInnerPart(index: Int, part: String) = ""

	override fun print(word: Word) = word.parts
		.mapIndexed(::transformPartToPrint)
		.foldIndexed(StringBuffer()) { index, left, right -> left.append(printBeforePart(index, right)).append(right) }
		.toString()
}

internal fun String.capitalizeOnlyFirst() = if (isNotEmpty()) this[0].toUpperCase() + substring(1).toLowerCase() else this

private val camelCaseSplitRegex = Regex("(?<=.)(?=\\p{Lu})")

object UpperCamelCase: BaseStringNotation(camelCaseSplitRegex) {
	public override fun transformPartToPrint(index: Int, part: String) = part.capitalizeOnlyFirst()
}

object LowerCamelCase: BaseStringNotation(camelCaseSplitRegex) {
	override fun transformPartToPrint(index: Int, part: String) = if (index == 0) part.toLowerCase() else part.capitalizeOnlyFirst()
}

object ScreamingSnakeCase: BaseStringNotation(Regex("_")) {
	override fun printBeforeInnerPart(index: Int, part: String) = "_"

	override fun transformPartToPrint(index: Int, part: String) = part.toUpperCase()
}

object SnakeCase: BaseStringNotation(Regex("_")) {
	override fun transformPartAfterParse(index: Int, part: String) = part
	override fun printBeforeInnerPart(index: Int, part: String) = "_"
}

object JavaTypeName: BaseStringNotation(camelCaseSplitRegex) {
	override fun transformPartToPrint(index: Int, part: String) = part.keepOnlyJavaIdentifierChars().capitalizeOnlyFirst()
}

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
