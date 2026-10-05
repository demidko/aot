package com.github.demidko.aot;

import java.security.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

/** Offline whole-dictionary differential oracle. Run separately on baseline and candidate. */
public class DictionaryDigest {
  private static final MessageDigest digest;
  private static final byte[] number = new byte[8];
  static {
    try { digest = MessageDigest.getInstance("SHA-256"); }
    catch (NoSuchAlgorithmException e) { throw new AssertionError(e); }
  }
  private static void integer(long value) {
    for (int i = 0; i < 8; i++) { number[i] = (byte)value; value >>>= 8; }
    digest.update(number);
  }
  private static void string(String value) {
    byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
    integer(bytes.length); digest.update(bytes);
  }
  private static void meaning(WordformMeaning m) throws Exception {
    integer(m.getId());
    integer(m.hashCode());
    string(m.toString());
    integer(m.getMorphology().size());
    for (Object tag : m.getMorphology()) string(tag.toString());
    string(String.valueOf(m.getPartOfSpeech()));
    if (!m.equals(WordformMeaning.lookupForMeaning(m.getId()))) throw new AssertionError("ID round trip");
  }
  public static void main(String[] args) throws Exception {
    Set<Long> seenLemmas = new HashSet<>();
    List<String> words = WordformMeaning.listAllFlexions();
    integer(words.size());
    long analyses = 0, transformations = 0;
    for (String word : words) {
      string(word);
      List<WordformMeaning> meanings = WordformMeaning.lookupForMeanings(word);
      integer(meanings.size());
      analyses += meanings.size();
      for (WordformMeaning m : meanings) {
        meaning(m);
        integer(m.getLemma().getId());
        if (seenLemmas.add(m.getLemma().getId())) {
          List<WordformMeaning> forms = m.getTransformations();
          integer(forms.size());
          transformations += forms.size();
          for (WordformMeaning form : forms) meaning(form);
        }
      }
    }
    StringBuilder hex = new StringBuilder();
    for (byte b : digest.digest()) hex.append(String.format(Locale.ROOT, "%02x", b & 255));
    System.out.printf("words=%d analyses=%d lemmas=%d transformations=%d sha256=%s%n", words.size(), analyses, seenLemmas.size(), transformations, hex);
    RuntimeEvidence.print();
  }
}
