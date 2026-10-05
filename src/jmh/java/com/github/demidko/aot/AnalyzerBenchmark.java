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
public class AnalyzerBenchmark {
  private String[] words;
  private WordformMeaning[] meanings;
  private long[] ids;
  private int cursor;
  // Original prose, tokenized in advance: ordinary usage, case changes, and unknowns.
  private final String[] prose = ("Утром люди шли по улице и говорили о работе " +
      "В старом доме у реки дети читали книги а родители готовили обед " +
      "Мы хотели бы увидеть новый город но поезд уже ушёл " +
      "Она сказала что завтра будет хорошая погода и можно пойти в парк " +
      "Инженеры проверили программу нашли ошибку и написали тесты " +
      "Система должна быстро находить формы слов сохраняя все варианты разбора " +
      "У каждого человека есть свои вопросы идеи и планы на будущее " +
      "Он взял ключ открыл замок и поставил сумку возле двери " +
      "В магазине было пять больших яблок две бутылки воды и свежий хлеб " +
      "Неизвестный_токен ChatGPT 2026 тоже встречаются в пользовательском тексте").split(" ");
  @Setup public void setup() {
    List<String> all = WordformMeaning.listAllFlexions();
    words = new String[1024];
    meanings = new WordformMeaning[1024];
    ids = new long[1024];
    for (int i = 0; i < words.length; i++) {
      words[i] = all.get((int) ((long) i * all.size() / words.length));
      meanings[i] = WordformMeaning.lookupForMeanings(words[i]).get(0);
      ids[i] = meanings[i].getId();
    }
  }
  @Benchmark public Object lookup() { return WordformMeaning.lookupForMeanings(words[cursor++ & 1023]); }
  @Benchmark public Object prose() {
    String word = prose[cursor];
    if (++cursor == prose.length) cursor = 0;
    return WordformMeaning.lookupForMeanings(word);
  }
  @Benchmark public Object unknown() { return WordformMeaning.lookupForMeanings("несуществующееслово123"); }
  @Benchmark public Object ambiguous() { return WordformMeaning.lookupForMeanings("стали"); }
  @Benchmark public Object normalized() { return WordformMeaning.lookupForMeanings("ЁЛКА"); }
  @Benchmark public long encodeId() { return meanings[cursor++ & 1023].getId(); }
  @Benchmark public Object decodeId() throws Exception { return WordformMeaning.lookupForMeaning(ids[cursor++ & 1023]); }
  @Benchmark public int meaningHash() { return meanings[cursor++ & 1023].hashCode(); }
  @Benchmark public Object transformations() { return meanings[cursor++ & 1023].getTransformations(); }
}
