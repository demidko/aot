package com.github.demidko.aot;

import org.openjdk.jmh.annotations.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 3, jvmArgsAppend = {"-Xms1g", "-Xmx1g"})
public class LookupReviewBenchmark {
  @Param({"0", "10", "50", "100"}) public int missPercent;
  private String[] inputs;
  private int cursor;
  @Setup public void setup() throws Exception {
    List<String> known = new ArrayList<>(), unknown = new ArrayList<>();
    Map<String, Integer> unknownCategories = new TreeMap<>();
    try (BufferedReader in = new BufferedReader(new InputStreamReader(
        getClass().getResourceAsStream("/review-corpus.tsv"), StandardCharsets.UTF_8))) {
      String line;
      while ((line = in.readLine()) != null) {
        String[] fields = line.split("\t", -1);
        int count = Integer.parseInt(fields[2]);
        if (WordformMeaning.lookupForMeanings(fields[1]).size() != count)
          throw new AssertionError("Corpus changed: " + fields[1]);
        if (count == 0) {
          unknown.add(fields[1]);
          unknownCategories.merge(fields[0], 1, Integer::sum);
        } else known.add(fields[1]);
      }
    }
    Random random = new Random(0x719a07);
    List<String> shuffled = new ArrayList<>();
    for (int i = 0; i < 1000; i++) {
      List<String> pool = i < missPercent * 10 ? unknown : known;
      shuffled.add(pool.get(random.nextInt(pool.size())));
    }
    Collections.shuffle(shuffled, random);
    inputs = shuffled.toArray(new String[0]);
    System.err.printf("known_pool=%d unknown_pool=%d miss_percent=%d unknown_categories=%s%n",
        known.size(), unknown.size(), missPercent, unknownCategories);
  }
  @Benchmark public Object freshToken() {
    String input = inputs[cursor];
    if (++cursor == inputs.length) cursor = 0;
    // Includes external-token construction, avoiding dictionary-string identity and cached query hashes.
    return WordformMeaning.lookupForMeanings(new String(input.toCharArray()));
  }
}
