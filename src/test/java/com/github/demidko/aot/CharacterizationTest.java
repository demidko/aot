package com.github.demidko.aot;

import org.junit.jupiter.api.Test;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import static com.github.demidko.aot.WordformMeaning.*;
import static org.junit.jupiter.api.Assertions.*;

/** Compatibility observations, not a claim that every dictionary analysis is linguistically correct. */
class CharacterizationTest {
  static final String[] WORDS = ("замок люди человека детей ребенку шла идти бежавший прочитанный " +
      "читать любить спать убийца сирота коллега судья кот кошка стол аватар автозаказ прочитав красивее лучший красивыми моя его все всё ел ёлка ЁЛКА подъезд съешь " +
      "кто-нибудь Санкт-Петербург Ростов-на-Дону Иванов Мария пальто ножницы двое пять первый " +
      "стали пила печь косой атлас мука вести мой три сорок что бы быстро нельзя увы и в " +
      "из-за по-русски пол-яблока 123 1-й неологизмнесуществующий").split(" ");

  static String signature(String word) throws Exception {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    for (WordformMeaning m : lookupForMeanings(word)) {
      add(digest, m);
      add(digest, m.getLemma());
      for (WordformMeaning t : m.getTransformations()) add(digest, t);
      digest.update((byte) '\n');
    }
    StringBuilder hex = new StringBuilder();
    for (byte b : digest.digest()) hex.append(String.format(Locale.ROOT, "%02x", b & 255));
    return lookupForMeanings(word).size() + "\t" + hex;
  }

  static void add(MessageDigest digest, WordformMeaning m) {
    String value = m.getId() + "|" + m + "|" + m.getMorphology() + "|" + m.getPartOfSpeech() + "\n";
    digest.update(value.getBytes(StandardCharsets.UTF_8));
  }

  /** Deliberate fixture generation only; never called by a test. Run on the recorded baseline. */
  public static void main(String[] args) throws Exception {
    List<String> words = new ArrayList<>(Arrays.asList(WORDS));
    List<String> all = listAllFlexions();
    // Spread across the entire dictionary, independent of hash iteration and RNG implementation.
    for (int i = 0; i < 512; i++) words.add(all.get((int) ((long) i * all.size() / 512)));
    List<String> lines = new ArrayList<>();
    for (String word : words) lines.add(word + "\t" + signature(word));
    Files.write(Paths.get(args[0]), lines, StandardCharsets.UTF_8);
    if (args.length > 1) {
      List<String> details = new ArrayList<>();
      for (String word : WORDS) details.add(word + "\t" + readable(word));
      Files.write(Paths.get(args[1]), details, StandardCharsets.UTF_8);
    }
  }

  static String readable(String word) {
    StringJoiner result = new StringJoiner("; ");
    for (WordformMeaning m : lookupForMeanings(word))
      result.add(m.getId() + "|" + m + "|" + m.getLemma().getId() + "|" + m.getLemma()
          + "|" + m.getMorphology() + "|" + m.getPartOfSpeech());
    return result.toString();
  }

  @Test void readableCuratedAnalyses() throws Exception {
    try (BufferedReader in = new BufferedReader(new InputStreamReader(
        getClass().getResourceAsStream("/analyses.tsv"), StandardCharsets.UTF_8))) {
      String line;
      int count = 0;
      while ((line = in.readLine()) != null) {
        String[] fields = line.split("\t", -1);
        assertEquals(fields[1], readable(fields[0]), fields[0]);
        count++;
      }
      assertEquals(WORDS.length, count);
    }
  }

  @Test void frozenOrderedAnalysesAndCompleteParadigms() throws Exception {
    try (BufferedReader in = new BufferedReader(new InputStreamReader(
        getClass().getResourceAsStream("/characterization.tsv"), StandardCharsets.UTF_8))) {
      String line;
      int count = 0;
      while ((line = in.readLine()) != null) {
        String[] fields = line.split("\t", 2);
        assertEquals(fields[1], signature(fields[0]), fields[0]);
        count++;
      }
      assertTrue(count > 550);
    }
  }

  @Test void normalizationAndMalformedInputs() {
    for (Locale locale : Arrays.asList(Locale.ROOT, Locale.US, Locale.forLanguageTag("tr-TR"))) {
      Locale previous = Locale.getDefault();
      try {
        Locale.setDefault(locale);
        assertEquals(lookupForMeanings("елка"), lookupForMeanings("ЁЛКА"));
        assertEquals(lookupForMeanings("замок"), lookupForMeanings("ЗаМоК"));
      } finally { Locale.setDefault(previous); }
    }
    for (String word : Arrays.asList("", " ", " замок", "замок ", "замок!", "замок\n", "😀", "\u0000", "\ud800", "е\u0308лка", "елкa", "qwerty", "сверхнеизвестноеслово"))
      assertTrue(lookupForMeanings(word).isEmpty(), word);
    assertThrows(NullPointerException.class, () -> lookupForMeanings(null));
  }

  @Test void externalHashCollisionDoesNotCreateAnAnalysis() {
    assertEquals("земля".hashCode(), "земма".hashCode());
    assertFalse(lookupForMeanings("земля").isEmpty());
    assertTrue(lookupForMeanings("земма").isEmpty());
  }

  @Test void idsEqualityAndParadigmOrder() throws Exception {
    for (String word : WORDS) for (WordformMeaning m : lookupForMeanings(word)) {
      assertEquals(m, lookupForMeaning(m.getId()));
      assertEquals(m.hashCode(), lookupForMeaning(m.getId()).hashCode());
      assertNotEquals(m, null);
      assertNotEquals(m, m.toString());
      assertEquals(m.getLemma(), m.getTransformations().get(0));
      assertTrue(m.getTransformations().contains(m));
      for (WordformMeaning t : m.getTransformations()) {
        assertEquals(t, lookupForMeaning(t.getId()));
        assertEquals(m.getLemma(), t.getLemma());
        assertTrue(lookupForMeanings(t.toString()).contains(t), t.toString());
      }
    }
    // Invalid indices are currently accepted, with failure deferred to access.
    for (long id : new long[]{0, 1, -1, Long.MIN_VALUE, Long.MAX_VALUE, 0xffffffffL, 0x123456789abcdef0L})
      assertEquals(id, lookupForMeaning(id).getId());
    assertThrows(IndexOutOfBoundsException.class, () -> lookupForMeaning(-1).toString());
  }

  @Test void homonymyMultiplicityAndSuppletion() {
    List<WordformMeaning> castle = lookupForMeanings("замок");
    assertEquals(5, castle.size());
    assertEquals(5, new HashSet<>(castle).size());
    assertEquals("замокнуть", castle.get(4).getLemma().toString());
    assertEquals("человек", lookupForMeanings("люди").get(0).getLemma().toString());
    List<WordformMeaning> forms = castle.get(0).getTransformations();
    assertEquals(24, forms.size());
    assertEquals(forms.get(0).toString(), forms.get(1).toString());
    assertNotEquals(forms.get(0), forms.get(1));
  }

  @Test void sameHashStringReplacementRemainsVisibleToLookup() {
    List<String> strings = listAllFlexions();
    int index = strings.indexOf("земля");
    assertTrue(index >= 0);
    List<WordformMeaning> previous = lookupForMeanings("земля");
    assertFalse(previous.isEmpty());
    try {
      strings.set(index, "земма");
      assertEquals(previous, lookupForMeanings("земма"));
      assertTrue(lookupForMeanings("земля").isEmpty());
    } finally { strings.set(index, "земля"); }
    assertEquals(previous, lookupForMeanings("земля"));
  }

  @Test void returnedListBehaviorObservedNotRecommended() {
    List<WordformMeaning> meanings = lookupForMeanings("замок");
    meanings.clear();
    assertEquals(5, lookupForMeanings("замок").size());
    assertThrows(UnsupportedOperationException.class, () -> lookupForMeanings("замок").get(0).getTransformations().clear());
    List<String> strings = listAllFlexions();
    String original = strings.get(0);
    try {
      strings.set(0, "temporary-characterization-value");
      assertEquals("temporary-characterization-value", listAllFlexions().get(0));
    } finally { strings.set(0, original); }
    assertThrows(UnsupportedOperationException.class, () -> strings.add("x"));
    WordformMeaning m = lookupForMeanings("люди").get(0);
    List<WordformMeaning> forms = m.getTransformations();
    WordformMeaning first = forms.get(0);
    forms.set(0, null);
    assertEquals(first, m.getTransformations().get(0)); // This view is local, unlike dictionary arrays.
    List<com.github.demidko.aot.morphology.MorphologyTag> tags = m.getMorphology();
    com.github.demidko.aot.morphology.MorphologyTag old = tags.get(0);
    try {
      tags.set(0, com.github.demidko.aot.morphology.MorphologyTag.Verb);
      assertEquals(com.github.demidko.aot.morphology.MorphologyTag.Verb, m.getMorphology().get(0));
      assertEquals(com.github.demidko.aot.morphology.MorphologyTag.Verb,
          lookupForMeanings("люди").get(0).getMorphology().get(0));
    } finally { tags.set(0, old); }
  }
}
