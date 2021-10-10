package com.github.demidko.aot;

import static com.github.demidko.aot.PartOfSpeech.partOfSpeech;
import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.List;

/**
 * Словоформа исходной леммы
 *
 * @deprecated лучше используйте современный API из {@link WordformMeaning}
 */
@Deprecated
public class Flexion {

  // оптимизация
  static HashDictionary db;

  private final int strIndex, tagsIndex;
  private final PartOfSpeech pos;

  Flexion(int source, int tags) {
    this.strIndex = source;
    this.tagsIndex = tags;
    pos = partOfSpeech(db.allMorphologyTags[tags]);
  }

  public List<MorphologyTag> getTags() {
    return asList(db.allMorphologyTags[tagsIndex]);
  }

  /**
   * @param tag тег
   * @return входит ли тег в множество тегов которыми помечена флексия
   */
  public boolean has(MorphologyTag tag) {
    // пустой тег однозначно входит в множество тегов
    if (tag == null) {
      return true;
    }
    for (MorphologyTag i : db.allMorphologyTags[tagsIndex]) {
      if (i == tag) {
        return true;
      }
    }
    return false;
  }

  public boolean hasNot(MorphologyTag tag) {
    return !has(tag);
  }

  public boolean hasAllOf(MorphologyTag... tags) {
    return Arrays.stream(tags).allMatch(this::has);
  }

  public boolean hasAnyOf(MorphologyTag... tags) {
    return Arrays.stream(tags).anyMatch(this::has);
  }

  public boolean hasNoneOf(MorphologyTag... tags) {
    return !hasAnyOf(tags);
  }

  @Override
  public String toString() {
    return db.allFlexionStrings[strIndex];
  }

  public PartOfSpeech getPartOfSpeech() {
    return pos;
  }
}
