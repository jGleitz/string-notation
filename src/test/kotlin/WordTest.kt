package de.joshuagleitze.stringnotation

import ch.tutteli.atrium.api.fluent.en_GB.asIterable
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.api.fluent.en_GB.notToBe
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.junit.jupiter.api.Test

class WordTest {
	@Test
	fun `implements #equals`() {
		val aInstance = Word("a")
		expect(aInstance).toBe(aInstance)
		expect(aInstance).toBe(Word("a"))
		expect(Word("a")).notToBe(Word("A"))
	}

	@Test
	fun `implements #hashCode`() {
		expect(Word("a"))
			.feature(Word::hashCode)
			.toBe(Word("a").hashCode())
	}

	@Test
	fun `exposes parts as list`() {
		expect(Word("with", "parts")).feature(Word::partsList).containsExactly("with", "parts")
		expect(Word(listOf("with", "parts"))).feature(Word::partsList).containsExactly("with", "parts")
		expect(Word(sequenceOf("with", "parts"))).feature(Word::partsList).containsExactly("with", "parts")
	}

	@Test
	fun `exposes parts as sequence`() {
		expect(Word("with", "parts")).feature(Word::parts).asIterable().containsExactly("with", "parts")
		expect(Word(listOf("with", "parts"))).feature(Word::parts).asIterable().containsExactly("with", "parts")
		expect(Word(sequenceOf("with", "parts"))).feature(Word::parts).asIterable().containsExactly("with", "parts")
	}

	@Test
	fun `allows to add parts`() {
		expect((Word("with") + "more" + "parts"))
			.toBe(Word("with", "more", "parts"))
	}

	@Test
	fun `allows to add words`() {
		expect(Word("with") + Word("more", "parts"))
			.toBe(Word("with", "more", "parts"))
	}

	@Test
	fun `allows to map parts`() {
		expect(Word("a", "b", "c"))
			.feature(Word::mapParts, String::toUpperCase)
			.toBe(Word("A", "B", "C"))
	}

	@Test
	fun `allows to flatMap parts`() {
		expect(Word("a", "b"))
			.feature(Word::flatMapParts) { it -> sequenceOf("${it}1", "${it}2") }
			.toBe(Word("a1", "a2", "b1", "b2"))
	}

	@Test
	fun `allows to parse parts from a notation`() {
		expect("these are words with UpperCamelCase")
			.feature(String::fromNotation, NormalWords)
			.feature(Word::partsFromNotation, UpperCamelCase)
			.toBe(Word("these", "are", "words", "with", "upper", "camel", "case"))
	}
}
