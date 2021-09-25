package com.github.demidko.aot;

import static java.util.Arrays.asList;
import static java.util.Objects.hash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * Словоформа одного определенного смысла. Зачем нужна эта абстракция вместо простого слова? Например, у слова "замок"
 * может быть три смысла: 1. Замок как строение. 2. Замок как устройство для запора дверей. 3. Замок, как форма слова
 * "замокнуть", например, "замок под дождем". Данный класс разрешает такие коллизии, позволяя получить исходную форму и
 * морфологические теги, также, при сравнении двух идентичных слофоформ разных смыслов, будет получен отрицательный
 * результат.
 */
public class WordformMeaning {

  /**
   * Словарь морфологии. При вызове из методов экземпляра всегда инициализирован. При вызове из статических методов,
   * нужно использовать {@link WordformMeaning#getDictionary()}.
   */
  private static HashDictionary dictionary;

  private final int lemmaId;

  private final int flexionIndex;

  private WordformMeaning(int lemmaId, int flexionIndex) {
    this.lemmaId = lemmaId;
    this.flexionIndex = flexionIndex;
  }

  /**
   * @return Уникальный идентификатор, по которому можно восстановить словоформу, даже после перезапуска приложения.
   * Идентификатор состоит из 48 бит (32 бита индекс леммы, 16 бит смещение трансформации) записанных по порядку в
   * long.
   */
  public long getId() {
    BitWriter w = new BitWriter();
    w.writeInt(lemmaId);
    w.writeInt(flexionIndex);
    return w.toLong();
  }

  /**
   * @return Текстовая запись слова (может быть общей с другими смыслами, напр. "замок" и "замок").
   */
  @Override
  public String toString() {
    return dictionary.getFlexionString(lemmaId, flexionIndex);
  }

  /**
   * @return Исходная смысловая форма, первого лица, единственного числа. Например, для слова (и смысла) "яблоками"
   * леммой будет являться запись "яблоко"
   */
  public WordformMeaning getLemma() {
    return new WordformMeaning(lemmaId, 0);
  }

  /**
   * @return Морфологические характеристики для заданного слова, род, число, падеж, спряжение и т. д.
   */
  public List<MorphologyTag> getMorphology() {
    return asList(dictionary.getFlexionTags(lemmaId, flexionIndex));
  }

  /**
   * @return Варианты трансформации словоформы по правилам русского языка, всевозможные склонения, спряжения и т. д.
   */
  public List<WordformMeaning> getTransformations() {
    int size = dictionary.fexionsSize(lemmaId);
    WordformMeaning[] res = new WordformMeaning[size];
    for (int i = 0; i < size; ++i) {
      res[i] = new WordformMeaning(lemmaId, i);
    }
    return asList(res);
  }

  /**
   * Метод ищет все возможные значения слова
   *
   * @param w слово в любой форме
   * @return список смыслов
   */
  public static List<WordformMeaning> lookupForMeanings(String w) throws IOException {
    ArrayList<WordformMeaning> result = new ArrayList<>();
    for (Entry<Integer, Integer> meaning : getDictionary().lookupForFlexions(w)) {
      result.add(new WordformMeaning(meaning.getKey(), meaning.getValue()));
    }
    return result;
  }

  /**
   * @param id идентификатор полученный ранее при помощи {@link WordformMeaning#getId()}
   * @return словоформа смысла
   */
  public static WordformMeaning lookupForMeaning(long id) throws IOException {
    BitReader reader = new BitReader(id);
    int lemmaId = reader.readInt();
    int flexionIndex = reader.readInt();
    getDictionary();
    return new WordformMeaning(lemmaId, flexionIndex);
  }

  /**
   * Инициализация {@link WordformMeaning#dictionary}
   *
   * @return словарь лемм и морфологии (низкоуровневое API)
   */
  static HashDictionary getDictionary() throws IOException {
    if (dictionary == null) {
      dictionary = new HashDictionary();
    }
    return dictionary;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WordformMeaning that = (WordformMeaning) o;
    return lemmaId == that.lemmaId && flexionIndex == that.flexionIndex;
  }

  @Override
  public int hashCode() {
    return hash(lemmaId, flexionIndex);
  }
}
