package com.github.demidko.aot;

import org.openjdk.jmh.annotations.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 3, jvmArgsAppend = {"-Xms1g", "-Xmx1g"})
public class IdReviewBenchmark {
  private WordformMeaning[] inputs;
  private int cursor;
  @Setup public void setup() {
    Random random = new Random(0x719a07);
    List<String> words = WordformMeaning.listAllFlexions();
    inputs = new WordformMeaning[8192];
    Set<Long> unique = new HashSet<>();
    long checksum = 0;
    for (int i = 0; i < inputs.length; i++) {
      List<WordformMeaning> meanings = WordformMeaning.lookupForMeanings(words.get(random.nextInt(words.size())));
      inputs[i] = meanings.get(random.nextInt(meanings.size()));
      unique.add(inputs[i].getId());
      checksum = 31 * checksum + inputs[i].getId();
    }
    System.err.printf("shuffled_id_inputs=%d unique=%d checksum=%d%n", inputs.length, unique.size(), checksum);
  }
  @Benchmark public long encodeShuffled() { return inputs[cursor++ & 8191].getId(); }
  @Benchmark public long encodeShuffledNoInline() { return encodeAcrossCallBoundary(inputs[cursor++ & 8191]); }
  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  private static long encodeAcrossCallBoundary(WordformMeaning meaning) { return meaning.getId(); }
}
