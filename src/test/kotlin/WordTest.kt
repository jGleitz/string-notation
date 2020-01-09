package de.joshuagleitze.stringnotation

import ch.tutteli.atrium.api.cc.en_GB.asIterable
import ch.tutteli.atrium.api.cc.en_GB.containsExactly
import ch.tutteli.atrium.api.cc.en_GB.property
import ch.tutteli.atrium.verbs.expect
import org.junit.jupiter.api.Test

class WordTest {
	@Test
	fun `exposes parts as list`() {
		expect(Word("with", "parts")).property(Word::partsList).containsExactly("with", "parts")
		expect(Word(listOf("with", "parts"))).property(Word::partsList).containsExactly("with", "parts")
		expect(Word(sequenceOf("with", "parts"))).property(Word::partsList).containsExactly("with", "parts")
	}

	@Test
	fun `exposes parts as sequence`() {
		expect(Word("with", "parts")).property(Word::parts).asIterable().containsExactly("with", "parts")
		expect(Word(listOf("with", "parts"))).property(Word::parts).asIterable().containsExactly("with", "parts")
		expect(Word(sequenceOf("with", "parts"))).property(Word::parts).asIterable().containsExactly("with", "parts")
	}

	@Test
	fun `allows to add parts`() {
		expect((Word("with") + "more" + "parts")).property(Word::partsList).containsExactly("with", "more", "parts")
	}
}
