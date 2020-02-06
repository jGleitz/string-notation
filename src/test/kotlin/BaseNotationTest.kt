package de.joshuagleitze.stringnotation

import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseNotationTest(
	private val notation: StringNotation,
	private val unchangedWords: List<Pair<String, Word>>,
	private val parseOnlyWords: List<Pair<String, Word>> = emptyList(),
	private val printOnlyWords: List<Pair<Word, String>> = emptyList()
) {
	@ParameterizedTest(name = "\"{0}\" -> {1}")
	@MethodSource("parseWords")
	fun `parses words correctly`(input: String, expectedWord: Word) {
		expect(input.fromNotation(notation)) {
			feature(Word::partsList).toBe(expectedWord.partsList)
		}
	}

	@ParameterizedTest(name = "{1} -> \"{0}\"")
	@MethodSource("printWords")
	fun `prints words correctly`(sourceWord: Word, expectedResult: String) {
		expect(sourceWord) {
			feature(Word::toNotation, notation).toBe(expectedResult)
		}
	}

	@ParameterizedTest(name = "\"{0}\"")
	@MethodSource("unchangedWords")
	fun `parsing and printing a word written in this notation does not change the word`(word: String) {
		expect(word) {
			feature(String::fromNotation, notation) {
				feature(Word::toNotation, notation).toBe(word)
			}
		}
	}

	private fun parseWords() = asArguments(unchangedWords + parseOnlyWords)
	private fun printWords() = asArguments(unchangedWords.map { it.swap() } + printOnlyWords)
	private fun unchangedWords() = asArguments(unchangedWords)

	private fun asArguments(pairs: List<Pair<*, *>>) = pairs.map {
		Arguments.arguments(
			it.first,
			it.second
		)
	}

	private fun <First, Second> Pair<First, Second>.swap() = Pair(this.second, this.first)
}
