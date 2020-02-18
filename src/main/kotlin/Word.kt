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
	 * Creates a new word, with all its parts transformed by the provided [transform] function.
	 */
	fun mapParts(transform: (String) -> String) = Word(parts.map(transform))

	/**
	 * Creates a new word, with all its parts transformed by the provided [transform] function, which may return more than one new part for
	 * every existing part.
	 */
	fun flatMapParts(transform: (String) -> Sequence<String>) = Word(parts.flatMap(transform))

	/**
	 * Creates a new word, with all its parts parsed by the provided [notation]. Allows to parse words that use a combination of notations.
	 */
	fun partsFromNotation(notation: StringNotation) = Word(parts.flatMap { it.fromNotation(notation).parts })

	/**
	 * Creates a copy of this word with the provided [part] appended.
	 */
	operator fun plus(part: String) = Word(parts + part)

	/**
	 * Creates a copy of this word with all provided [parts] appended.
	 */
	fun plus(vararg parts: String) = Word(this.parts + parts)

	/**
	 * Creates a copy of this word with all parts of the provided [word] appended.
	 */
	operator fun plus(word: Word) = Word(parts + word.parts)

	override fun toString() = "Word(${parts.joinToString { "\"$it\"" }})"

	override fun equals(other: Any?) = this === other || (other is Word && partsList == other.partsList)

	override fun hashCode() = partsList.hashCode()
}

/**
 * Converts `this` string, into a [Word] according to the provided [notation]. This method expects that the input is formatted according to
 * the [notation] and creates a notation-agnostic representation of it.
 */
fun String.fromNotation(notation: StringNotation): Word = notation.parse(this)
