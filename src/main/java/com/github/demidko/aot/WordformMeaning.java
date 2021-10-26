package com.github.demidko.aot;

import static com.github.demidko.aot.ByteBlock.readBlockFrom;
import static com.github.demidko.aot.PartOfSpeech.partOfSpeech;
import static com.github.demidko.aot.Reader.readLemmas;
import static com.github.demidko.aot.Reader.readMorph;
import static com.github.demidko.aot.Reader.readRefs;
import static com.github.demidko.aot.Reader.readStrings;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Objects.hash;

import com.github.demidko.aot.bytecode.MorphologyTag;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Словоформа одного определенного смысла. Зачем нужна эта абстракция вместо простого слова? Например, у слова "замок"
 * может быть три основных смысла: 1. Замок как строение. 2. Замок как устройство для запора дверей. 3. Замок, как форма
 * слова "замокнуть", например, "замок под дождем". И это мы еще не учли возможную разницу в падежах. Данный класс
 * разрешает такие коллизии, позволяя получить исходную форму, морфологические теги и часть речи, при сравнении двух
 * идентичных словоформ разных смыслов, будет получен отрицательный результат.
 */
public class WordformMeaning {

  private static final MorphologyTag[][] allMorphologyTags;
  private static final String[] allFlexionStrings;
  private static final int[][] lemmas;
  private static final Map<Integer, int[]> refs;

  /**
   * Идентификатор леммы
   */
  private final int lemmaId;

  /**
   * Индекс трансформации леммы
   */
  private final int transformationIndex;

  static {
    try (DataInputStream file =
      new DataInputStream(new GZIPInputStream(WordformMeaning.class.getResourceAsStream("/mrd.gz")))
    ) {
      allMorphologyTags = readMorph(readBlockFrom(file));
      allFlexionStrings = readStrings(readBlockFrom(file));
      lemmas = readLemmas(readBlockFrom(file));
      refs = readRefs(readBlockFrom(file));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * @param lemmaId             Идентификатор леммы
   * @param transformationIndex Индекс трансформации леммы
   */
  private WordformMeaning(int lemmaId, int transformationIndex) {
    this.lemmaId = lemmaId;
    this.transformationIndex = transformationIndex;
  }

  private static int getTransformationsSize(int lemmaId) {
    return lemmas[lemmaId].length / 2;
  }

  private static String getTransformationString(int lemmaId, int transformationIndex) {
    return allFlexionStrings[lemmas[lemmaId][transformationIndex * 2]];
  }

  private static List<MorphologyTag> getTransformationMorphology(int lemmaId, int transformationIndex) {
    return asList(allMorphologyTags[lemmas[lemmaId][transformationIndex * 2 + 1]]);
  }

  /**
   * Метод ищет все возможные значения слова
   *
   * @param w слово в любой форме
   * @return список смыслов включая омонимии
   */
  public static List<WordformMeaning> lookupForMeanings(String w) throws IOException {
    w = w.toLowerCase().replace('ё', 'е');
    int[] ids = refs.get(w.hashCode());
    if (ids == null) {
      return emptyList();
    }
    List<WordformMeaning> meanings = new ArrayList<>();
    for (int lemmaId : ids) {
      for (int flexionIdx = 0; flexionIdx < getTransformationsSize(lemmaId); ++flexionIdx) {
        if (getTransformationString(lemmaId, flexionIdx).equals(w)) {
          meanings.add(new WordformMeaning(lemmaId, flexionIdx));
        }
      }
    }
    return meanings;
  }

  /**
   * Метод для получения словоформы по ее уникальному идентификатору
   *
   * @param id идентификатор полученный ранее
   * @return словоформ смысла
   */
  public static WordformMeaning lookupForMeaning(long id) throws IOException {
    BitReader reader = new BitReader(id);
    int lemmaId = reader.readInt();
    int flexionIndex = reader.readInt();
    return new WordformMeaning(lemmaId, flexionIndex);
  }


  /**
   * @return Уникальный идентификатор, по которому можно восстановить словоформу, даже после перезапуска приложения.
   * Идентификатор состоит из 48 бит (32 бита индекс леммы, 16 бит смещение трансформации) записанных по порядку в
   * примитив long.
   */
  public long getId() {
    BitWriter w = new BitWriter();
    w.writeInt(lemmaId);
    w.writeInt(transformationIndex);
    return w.toLong();
  }

  /**
   * @return Текстовая запись слова в lowercase (может быть общей с другими смыслами, напр. "замок" и "замок")
   */
  @Override
  public String toString() {
    return getTransformationString(lemmaId, transformationIndex);
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
    return getTransformationMorphology(lemmaId, transformationIndex);
  }

  /**
   * @return Варианты трансформации словоформы по правилам русского языка, всевозможные склонения, спряжения и т. д.
   */
  public List<WordformMeaning> getTransformations() {
    WordformMeaning[] res = new WordformMeaning[getTransformationsSize(lemmaId)];
    for (int i = 0; i < res.length; ++i) {
      res[i] = new WordformMeaning(lemmaId, i);
    }
    return asList(res);
  }

  /**
   * @return Часть речи к которой относится это слово, например существительное, глагол, и т. п.
   */
  public PartOfSpeech getPartOfSpeech() {
    return partOfSpeech(getMorphology());
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
    return lemmaId == that.lemmaId && transformationIndex == that.transformationIndex;
  }

  @Override
  public int hashCode() {
    return hash(lemmaId, transformationIndex);
  }
}
