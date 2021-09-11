package com.github.demidko.aot;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Морфологические данные смысла слова (их, и соответственно объектов Word, может быть несколько, как зАмок-строение,
 * зАмок-запор и замОк-промок)
 */
public class Word {

  private final List<Flexion> flexions;

  Word(int[] flexionsLinks) {
    Flexion[] flexions = new Flexion[flexionsLinks.length / 2];
    for (int i = 0, j = 0; i < flexions.length; ++i, j += 2) {
      flexions[i] = new Flexion(flexionsLinks[j], flexionsLinks[j + 1]);
    }
    this.flexions = asList(flexions);
  }

  /**
   * @return всевозможные варианты преобразования исходной формы слова
   */
  public List<Flexion> getFlexions() {
    return flexions;
  }

  /**
   * @return исходная форма слова, первое лицо, единственное число
   */
  public Flexion getLemma() {
    return flexions.get(0);
  }

  @SafeVarargs
  private static Predicate<Flexion> joinPredicates(Predicate<Flexion>... predicate) {
    return flex -> Arrays.stream(predicate).allMatch(p -> p.test(flex));
  }

  @SafeVarargs
  public final boolean anyFlexion(Predicate<Flexion>... predicate) {
    return flexions.stream().anyMatch(joinPredicates(predicate));
  }

  @SafeVarargs
  public final List<Flexion> getFlexions(Predicate<Flexion>... predicate) {
    return flexions.stream().filter(joinPredicates(predicate)).collect(toList());
  }
}
