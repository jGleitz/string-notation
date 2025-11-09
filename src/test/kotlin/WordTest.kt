package de.joshuagleitze.stringnotation

import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.expect
import org.junit.jupiter.api.Test

class WordTest {
	@Test
	fun `implements #equals`() {
		val aInstance = Word("a")
		expect(aInstance).toEqual(aInstance)
		expect(aInstance).toEqual(Word("a"))
		expect(Word("a")).notToEqual(Word("A"))
	}

	@Test
	fun `implements #hashCode`() {
		expect(Word("a"))
			.feature(Word::hashCode)
			.toEqual(Word("a").hashCode())
	}

	@Test
	fun `exposes parts as list`() {
		expect(Word("with", "parts")).feature(Word::partsList).toContainExactly("with", "parts")
		expect(Word(listOf("with", "parts"))).feature(Word::partsList).toContainExactly("with", "parts")
		expect(Word(sequenceOf("with", "parts"))).feature(Word::partsList).toContainExactly("with", "parts")
	}

	@Test
	fun `exposes parts as sequence`() {
		expect(Word("with", "parts")).feature(Word::parts).asIterable().toContainExactly("with", "parts")
		expect(Word(listOf("with", "parts"))).feature(Word::parts).asIterable().toContainExactly("with", "parts")
		expect(Word(sequenceOf("with", "parts"))).feature(Word::parts).asIterable().toContainExactly("with", "parts")
	}

	@Test
	fun `allows to add parts`() {
		expect((Word("with") + "more" + "parts"))
			.toEqual(Word("with", "more", "parts"))
		expect(Word("with").plus("more", "parts"))
			.toEqual(Word("with", "more", "parts"))
	}

	@Test
	fun `allows to add words`() {
		expect(Word("with") + Word("more", "parts"))
			.toEqual(Word("with", "more", "parts"))
	}

	@Test
	fun `allows to map parts`() {
		expect(Word("a", "b", "c"))
			.feature(Word::mapParts, String::uppercase)
			.toEqual(Word("A", "B", "C"))
	}

	@Test
	fun `allows to flatMap parts`() {
		expect(Word("a", "b"))
			.feature(Word::flatMapParts) { sequenceOf("${it}1", "${it}2") }
			.toEqual(Word("a1", "a2", "b1", "b2"))
	}

	@Test
	fun `allows to parse parts from a notation`() {
		expect("these are words with UpperCamelCase")
			.feature(String::fromNotation, NormalWords)
			.feature(Word::partsFromNotation, UpperCamelCase)
			.toEqual(Word("these", "are", "words", "with", "upper", "camel", "case"))
	}
}
