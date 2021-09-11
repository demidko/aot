package com.github.demidko.aot;

import java.util.function.Predicate;


public final class Matchers {

	public static Predicate<Flexion> tags(MorphologyTag... tags) {
		return flexion -> flexion.hasAllOf(tags);
	}

	public static Predicate<Flexion> partOfSpeech(PartOfSpeech pos) {
		return flexion -> flexion.getPartOfSpeech() == pos;
	}

	public static Predicate<Flexion> string(String str) {
		return flexion -> flexion.toString().equals(str);
	}

	public static Predicate<Flexion> has(Predicate<Flexion> predicate) {
		return predicate;
	}

	public static Predicate<Flexion> not(Predicate<Flexion> predicate) {
		return predicate.negate();
	}
}
