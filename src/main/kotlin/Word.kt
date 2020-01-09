package de.joshuagleitze.stringnotation

class Word(val parts: Sequence<String>) {
	constructor(vararg parts: String): this(parts.asSequence())
	constructor(parts: List<String>): this(parts.asSequence())

	val partsList get() = parts.toList()

	fun toNotation(notation: StringNotation) = notation.print(this)

	operator fun plus(word: String) = Word(parts + word)

	operator fun plus(word: Word) = Word(parts + word.parts)
}

fun String.inNotation(notation: StringNotation): Word = notation.parse(this)
