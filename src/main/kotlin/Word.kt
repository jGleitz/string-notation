@file:JvmName("StringToWord")

package de.joshuagleitze.stringnotation

/**
 * A notation-agnostic representation of a string. This is a “word” in the sense of “word over the Unicode alphabet”, not in the sense of a
 * word in any spoken language. A `Word` consists of [parts].
 *
 * @property parts The different parts this word consists of. A [StringNotation] will [parse][StringNotation.parse] a given input into its
 * [parts] and [print][StringNotation.print] a `Word` by combining its [parts] appropriately.
 */
class Word(val parts: Sequence<String>) {
	constructor(vararg parts: String): this(parts.asSequence())
	constructor(parts: List<String>): this(parts.asSequence())

	/**
	 * Gives the [parts] as a [List].
	 */
	val partsList get() = parts.toList()

	/**
	 * Converts this word into a string according to the given [notation].
	 */
	fun toNotation(notation: StringNotation) = notation.print(this)

	/**
	 * Appends a part to this word.
	 */
	operator fun plus(part: String) = Word(parts + part)

	/**
	 * Appends all parts of the given [word] to this word.
	 */
	operator fun plus(word: Word) = Word(parts + word.parts)

	override fun toString() = "Word(${parts.joinToString { "\"$it\"" }})"
}

/**
 * Converts `this` string, into a [Word] according to the provided [notation]. This method expects that the input is formatted according to
 * the [notation] and creates a notation-agnostic representation of it.
 */
fun String.fromNotation(notation: StringNotation): Word = notation.parse(this)
