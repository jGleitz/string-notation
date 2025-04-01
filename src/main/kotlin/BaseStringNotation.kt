package de.joshuagleitze.stringnotation

/**
 * Base class for implementing string notations.
 *
 * @constructor Creates a string notation that will use the provided regular expression to [split][String.split] parts when parsing.
 */
abstract class BaseStringNotation(private val splitAt: Regex): StringNotation {
	/**
	 * Transforms a parsed part after it has been read. The default implementation does not change the part.
	 */
	protected open fun transformPartAfterParse(index: Int, part: String) = part

	override fun parse(sourceString: String): Word =
		Word(sourceString.split(splitAt).asSequence().filter(String::isNotBlank).mapIndexed(::transformPartAfterParse))

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
		.foldIndexed(StringBuffer()) { index, existing, part -> existing.append(printBeforePart(index, part)).append(part) }
		.toString()

	override fun toString(): String = this::class.java.simpleName
}
