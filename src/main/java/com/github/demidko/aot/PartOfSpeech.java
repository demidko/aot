package com.github.demidko.aot;

public enum PartOfSpeech {
	Pronoun,
	ShortAdjective,
	Interjection,
	Adverb,
	Verb,
	Adjective,
	Noun,
	Introduction,
	PronounPredicative,
	PronounAdjective,
	Participle,
	VerbalParticiple,
	BreifComunion,
	POSL_PART_OF_SPEECH,
	Predicative,
	Particle,
	Numeral,
	Pretext,
	Infinitive,
	OrdinalNumber,
	Idiom,
	Union;

	public static PartOfSpeech from(MorphologyTag tag) {
		switch (tag) {
			case Pronoun:
				return Pronoun;
			case PronounAdjective:
				return PronounAdjective;
			case ShortAdjective:
				return ShortAdjective;
			case Introduction:
				return Introduction;
			case Interjection:
				return Interjection;
			case Participle:
				return Participle;
			case Infinitive:
				return Infinitive;
			case Adjective:
				return Adjective;
			case Numeral:
				return Numeral;
			case Adverb:
				return Adverb;
			case Union:
				return Union;
			case Idiom:
				return Idiom;
			case Verb:
				return Verb;
			case Noun:
				return Noun;
			case PronounPredicative:
				return PronounPredicative;
			case VerbalParticiple:
				return VerbalParticiple;
			case BreifComunion:
				return BreifComunion;
			case POSL_PART_OF_SPEECH:
				return POSL_PART_OF_SPEECH;
			case Predicative:
				return Predicative;
			case Particle:
				return Particle;
			case Pretext:
				return Pretext;
			case OrdinalNumber:
				return OrdinalNumber;
			default:
				return null;
		}
	}

	public static PartOfSpeech from(MorphologyTag[] tags) {
		for (MorphologyTag tag : tags) {
			PartOfSpeech maybe = from(tag);
			if (maybe != null) {
				return maybe;
			}
		}
		return null;
	}
}
