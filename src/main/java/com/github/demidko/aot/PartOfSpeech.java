package com.github.demidko.aot;

import com.github.demidko.aot.bytecode.MorphologyTag;

/**
 * Часть речи, например существительное, глагол и т. д.
 */
public enum PartOfSpeech {

  /**
   * Местоимение-существительное
   */
  Pronoun,

  /**
   * Краткое прилагательное
   */
  ShortAdjective,

  /**
   * Междометие
   */
  Interjection,

  /**
   * Наречие
   */
  Adverb,

  /**
   * Глагол в личной форме
   */
  Verb,

  /**
   * Прилагательное
   */
  Adjective,

  /**
   * Существительное
   */
  Noun,

  /**
   * Вводное слово
   */
  Introduction,

  /**
   * Местоимение-предикатив
   */
  PronounPredicative,

  /**
   * Местоименное прилагательное
   */
  PronounAdjective,

  /**
   * Причастие
   */
  Participle,

  /**
   * Деепричастие
   */
  VerbalParticiple,

  /**
   * Краткое причастие
   */
  BriefCommunion,

  /**
   * Некая часть речи (?)
   */
  POSL_PART_OF_SPEECH,

  /**
   * Предикатив
   */
  Predicative,

  /**
   * Частица
   */
  Particle,

  /**
   * Числительное (количественное)
   */
  Numeral,

  /**
   * Предлог
   */
  Pretext,

  /**
   * Инфинитив
   */
  Infinitive,

  /**
   * Порядковое числительное
   */
  OrdinalNumber,

  /**
   * Фразеологизм
   */
  Idiom,

  /**
   * Союз
   */
  Union;

  /**
   * Метод преобразует морфологический тег в часть речи
   *
   * @param tag тег морфологической информации
   * @return извлеченная часть речи или null
   */
  public static PartOfSpeech partOfSpeech(MorphologyTag tag) {
    switch (tag) {
      case Pronoun:
        return Pronoun;
      case PronounAdjective:
        return PronounAdjective;
      case ShortAdjective:
        return ShortAdjective;
      case Introduction:
        return Introduction;
      case Interjection:
        return Interjection;
      case Participle:
        return Participle;
      case Infinitive:
        return Infinitive;
      case Adjective:
        return Adjective;
      case Numeral:
        return Numeral;
      case Adverb:
        return Adverb;
      case Union:
        return Union;
      case Idiom:
        return Idiom;
      case Verb:
        return Verb;
      case Noun:
        return Noun;
      case PronounPredicative:
        return PronounPredicative;
      case VerbalParticiple:
        return VerbalParticiple;
      case BriefCommunion:
        return BriefCommunion;
      case POSL_PART_OF_SPEECH:
        return POSL_PART_OF_SPEECH;
      case Predicative:
        return Predicative;
      case Particle:
        return Particle;
      case Pretext:
        return Pretext;
      case OrdinalNumber:
        return OrdinalNumber;
      default:
        return null;
    }
  }

  /**
   * Метод извлекает часть речи из набора всей морфологической информации
   *
   * @param tags вся морфологическая информация
   * @return извлеченная часть речи или null
   */
  static PartOfSpeech partOfSpeech(Iterable<MorphologyTag> tags) {
    for (MorphologyTag tag : tags) {
      PartOfSpeech maybe = partOfSpeech(tag);
      if (maybe != null) {
        return maybe;
      }
    }
    return null;
  }
}
