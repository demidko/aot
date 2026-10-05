package com.github.demidko.aot;

import com.github.demidko.bits.BitReader;
import com.github.demidko.bits.BitWriter;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import static com.github.demidko.aot.WordformMeaning.*;
import static org.junit.jupiter.api.Assertions.*;

/** Independent legacy-ID oracle and read-only concurrency; also run against the baseline JAR. */
class CompatibilityBoundaryTest {
  private static long legacyId(int lemma, int transformation) {
    return new BitWriter().writeInt(lemma).writeInt(transformation).toLong();
  }

  private static void verifyId(long id) throws Exception {
    BitReader reader = new BitReader(id);
    int lemma = reader.readInt(), transformation = reader.readInt();
    WordformMeaning meaning = lookupForMeaning(id);
    assertEquals(legacyId(lemma, transformation), meaning.getId());
    assertEquals(Objects.hash(lemma, transformation), meaning.hashCode());
    assertEquals(legacyId(lemma, 0), meaning.getLemma().getId());
    assertEquals(meaning, lookupForMeaning(id));
  }

  @Test void arbitraryIdsMatchUnchangedBitsDependency() throws Exception {
    int[] boundaries = {Integer.MIN_VALUE, -65536, -1, 0, 1, 32767, 32768, 65535, 65536, Integer.MAX_VALUE};
    for (int lemma : boundaries) for (int form : boundaries) verifyId(legacyId(lemma, form));
    Random random = new Random(0x719a07);
    for (int i = 0; i < 4096; i++) verifyId(random.nextLong());
  }

  @Test void invalidIndicesRetainDeferredFailureBehavior() throws Exception {
    WordformMeaning valid = lookupForMeanings("люди").get(0).getLemma();
    BitReader reader = new BitReader(valid.getId());
    int lemma = reader.readInt(), size = valid.getTransformations().size();
    assertEquals(valid.getTransformations().get(size - 1), lookupForMeaning(legacyId(lemma, size - 1)));
    for (int form : new int[]{-1, Integer.MIN_VALUE + size, size, Integer.MAX_VALUE}) {
      WordformMeaning invalid = lookupForMeaning(legacyId(lemma, form));
      assertEquals(valid, invalid.getLemma());
      assertEquals(valid.getTransformations(), invalid.getTransformations());
      assertThrows(IndexOutOfBoundsException.class, invalid::toString);
      assertThrows(IndexOutOfBoundsException.class, invalid::getMorphology);
      assertThrows(IndexOutOfBoundsException.class, invalid::getPartOfSpeech);
    }
    // Observed defect, not recommended behavior: multiplying an extreme index by two wraps.
    // Preserve it here; introducing ID validation requires an explicit behavior change.
    for (int index : new int[]{0, size - 1}) {
      WordformMeaning aliased = lookupForMeaning(legacyId(lemma, Integer.MIN_VALUE + index));
      WordformMeaning normal = valid.getTransformations().get(index);
      assertEquals(normal.toString(), aliased.toString());
      assertEquals(normal.getMorphology(), aliased.getMorphology());
      assertEquals(normal.getPartOfSpeech(), aliased.getPartOfSpeech());
      assertNotEquals(normal, aliased);
    }
    // 174,628 is the frozen dictionary's lemma count, so it is the first invalid index.
    for (int badLemma : new int[]{-1, Integer.MIN_VALUE, 174628, Integer.MAX_VALUE}) {
      WordformMeaning invalid = lookupForMeaning(legacyId(badLemma, 0));
      assertEquals(invalid, invalid.getLemma());
      assertThrows(IndexOutOfBoundsException.class, invalid::toString);
      assertThrows(IndexOutOfBoundsException.class, invalid::getMorphology);
      assertThrows(IndexOutOfBoundsException.class, invalid::getTransformations);
    }
  }

  @Test void concurrentReadersMatchFrozenAnalyses() throws Exception {
    List<String[]> cases = new ArrayList<>();
    try (BufferedReader in = new BufferedReader(new InputStreamReader(
        getClass().getResourceAsStream("/analyses.tsv"), StandardCharsets.UTF_8))) {
      String line;
      while ((line = in.readLine()) != null) cases.add(line.split("\t", -1));
    }
    ExecutorService workers = Executors.newFixedThreadPool(4);
    try {
      List<Callable<Void>> jobs = new ArrayList<>();
      for (int thread = 0; thread < 4; thread++) jobs.add(() -> {
        for (int pass = 0; pass < 8; pass++) for (String[] entry : cases)
          assertEquals(entry[1], CharacterizationTest.readable(entry[0]), entry[0]);
        return null;
      });
      for (Future<Void> result : workers.invokeAll(jobs)) result.get();
    } finally { workers.shutdownNow(); }
  }

  @Test void malformedAndUnnormalizedUnicodeRemainsUnknown() {
    for (String word : Arrays.asList("\udc00", "\ud800\ud800", "\ud83d\ude00", "\u00a0замок", "замок\u00a0",
        "\u202eзамок", "за\u0301мок", "е\u0308лка", "елкa", "ёлка\u200b"))
      assertTrue(lookupForMeanings(word).isEmpty(), word);
    assertEquals(lookupForMeanings("и"), lookupForMeanings("И"));
  }
}
