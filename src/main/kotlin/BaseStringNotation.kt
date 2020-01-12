package de.joshuagleitze.stringnotation

import java.util.*

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

	override fun toString() = this::class.java.simpleName!!
}
