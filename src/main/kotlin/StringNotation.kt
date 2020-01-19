package de.joshuagleitze.stringnotation

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


