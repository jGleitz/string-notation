package de.joshuagleitze.stringnotation

import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(PER_CLASS)
abstract class BaseNotationTest(
	private val notation: StringNotation,
	private val unchangedWords: List<NotationTestData>,
	private val parseOnlyWords: List<NotationTestData> = emptyList(),
	private val printOnlyWords: List<NotationTestData> = emptyList()
) {
	@ParameterizedTest(name = "\"{1}\" -> {2}")
	@MethodSource("parseWords")
	fun `parses words correctly`(minimumJavaVersion: Int, input: String, expectedWord: Word) {
		assumeTrue(currentJavaVersion >= minimumJavaVersion, "Requires at least Java $minimumJavaVersion")
		expect(input.fromNotation(notation)) {
			feature(Word::partsList).toEqual(expectedWord.partsList)
		}
	}

	@ParameterizedTest(name = "{2} -> \"{1}\"")
	@MethodSource("printWords")
	fun `prints words correctly`(minimumJavaVersion: Int, expectedResult: String, sourceWord: Word) {
		assumeTrue(currentJavaVersion >= minimumJavaVersion, "Requires at least Java $minimumJavaVersion")
		expect(sourceWord) {
			feature(Word::toNotation, notation).toEqual(expectedResult)
		}
	}

	@ParameterizedTest(name = "\"{1}\"")
	@MethodSource("unchangedWords")
	fun `parsing and printing a word written in this notation does not change the word`(minimumJavaVersion: Int, word: String) {
		assumeTrue(currentJavaVersion >= minimumJavaVersion, "Requires at least Java $minimumJavaVersion")
		expect(word) {
			feature(String::fromNotation, notation) {
				feature(Word::toNotation, notation).toEqual(word)
			}
		}
	}

	private fun parseWords() = asArguments(unchangedWords + parseOnlyWords)
	private fun printWords() = asArguments(unchangedWords + printOnlyWords)
	private fun unchangedWords() = asArguments(unchangedWords)

	private fun asArguments(pairs: List<NotationTestData>) = pairs.map {
		Arguments.arguments(
			it.minimumJavaVersion,
			it.string,
			it.word
		)
	}
}

data class NotationTestData(val word: Word, val string: String, var minimumJavaVersion: Int = 0)

infix fun Word.to(string: String) = NotationTestData(this, string)
infix fun String.to(word: Word) = NotationTestData(word, this)
infix fun NotationTestData.ifJvmVersionIsAtLeast(minimumJavaVersion: Int) =
	this.apply { this.minimumJavaVersion = minimumJavaVersion }

val currentJavaVersion by lazy {
	System.getProperty("java.runtime.version")
		.split(".")
		.let { if (it[0] == "1") it.drop(1) else it }[0]
		.toInt()
}
