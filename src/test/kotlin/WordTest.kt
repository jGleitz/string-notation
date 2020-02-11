package de.joshuagleitze.stringnotation

import ch.tutteli.atrium.api.fluent.en_GB.asIterable
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.api.verbs.expect
import org.junit.jupiter.api.Test

class WordTest {
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
			.feature(Word::partsList)
			.containsExactly("with", "more", "parts")
	}

	@Test
	fun `allows to add words`() {
		expect(Word("with") + Word("more", "parts"))
			.feature(Word::partsList)
			.containsExactly("with", "more", "parts")
	}

	@Test
	fun `allows to transform parts`() {
		expect(Word("a", "b", "c"))
			.feature(Word::mapParts, String::toUpperCase)
			.feature(Word::partsList)
			.containsExactly("A", "B", "C");
	}
}
