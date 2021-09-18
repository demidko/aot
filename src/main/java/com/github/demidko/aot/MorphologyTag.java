package com.github.demidko.aot;

/**
 * Морфологическая характеристика для слова. Например {@link MorphologyTag#FirstPerson}
 */
public enum MorphologyTag {

  /* !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
   * В этом перечислении ни в коем случае нельзя менять (сдвигать) порядок элементов. От этого зависит корректность компиляции и
   * декомпиляции. Важно чтобы это перечисление актуализировалось при изменениях формата в
   * <a href="https://github.com/demidko/aot-binary/blob/main/src/main/kotlin/ml/demidko/aot/MorphologyTag.kt">компиляторе</a>
   */

  /**
   * Множественное число
   */
  Plural("мн"),

  /**
   * Мужской род
   */
  Male("мр"),

  /**
   * Именительный падеж
   */
  Nominative("им"),

  /**
   * Местоимение-существительное
   */
  Pronoun("МС"),

  /**
   * Аббревиатура
   */
  Abbreviation("аббр"),

  /**
   * Краткое прилагательное
   */
  ShortAdjective("КР_ПРИЛ"),

  /**
   * Сравнительная степень (для прилагательных)
   */
  Comparative("сравн"),

  /**
   * Дательный падеж
   */
  Dative("дт"),

  /**
   * Настоящее время
   */
  PresentTense("нст"),

  /**
   * Указательное наречие
   */
  IndicativeAdverb("указат"),

  /**
   * Совершенный вид
   */
  PerfectVerb("св"),

  /**
   * Отчество (Иванович, Михайлович)
   */
  MiddleName("отч"),

  /**
   * Глагол в личной форме
   */
  Verb("Г"),

  /**
   * Прошедшее время
   */
  PastTime("прш"),

  /**
   * Междометие
   */
  Interjection("МЕЖД"),

  /**
   * Наречие
   */
  Adverb("Н"),

  /**
   * Единственное число
   */
  Singular("ед"),

  /**
   * Сравнительная степень (для прилагательных)
   */
  NeuterGender("ср"),

  /**
   * Прилагательное
   */
  Adjective("П"),

  /**
   * Существительное
   */
  Noun("С"),

  /**
   * Неодушевленное
   */
  Inanimate("но"),

  /**
   * Организация
   */
  Organization("орг"),

  /**
   * Вводное слово
   */
  Introduction("ВВОДН"),

  /**
   * Непереходный глагол
   */
  Intransitive("нп"),

  /**
   * Несовершенный вид
   */
  Imperfect("нс"),

  /**
   * Архаизм
   */
  Archaism("арх"),

  /**
   * Топоним (Москва, Лена, Эверест)
   */
  Toponym("лок"),

  /**
   * Общий род (сирота, пьяница)
   */
  MaleFemale("мр-жр"),

  /**
   * Местоимение-предикатив
   */
  PronounPredicative("МС-ПРЕДК"),

  /**
   * Первое лицо
   */
  FirstPerson("1л"),

  /**
   * Безличный глагол (2)
   */
  ImpersonalVerb2("*"),

  /**
   * Страдательный залог
   */
  PassiveParticiple("стр"),

  /**
   * Творительный падеж
   */
  Ablative("тв"),

  /**
   * Неизменяемое
   */
  Immutable("0"),

  /**
   * Второй родительный или второй предложный падежи
   */
  SecondGenetive("2"),

  /**
   * Разговорный
   */
  ColloquialSpeech("разг"),

  /**
   * Одушевленное
   */
  Animated("од"),

  /**
   * Опечатка
   */
  Typo("опч"),

  /**
   * Местоименное прилагательное
   */
  PronounAdjective("МС-П"),

  /**
   * Причастие
   */
  Participle("ПРИЧАСТИЕ"),

  /**
   * Превосходная степень (для прилагательных)
   */
  SuperlativeAdjective("прев"),

  /**
   * Фамилия (Иванов, Сидоров)
   */
  Surname("фам"),

  /**
   * Качественное прилагательное
   */
  QualitativeAdjective("кач"),

  /**
   * Действительный залог
   */
  ActiveVoice("дст"),

  /**
   * Деепричастие
   */
  VerbalParticiple("ДЕЕПРИЧАСТИЕ"),

  /**
   * Краткое причастие
   */
  BreifComunion("КР_ПРИЧАСТИЕ"),

  /**
   * Прилагательное (?)
   */
  AdjectiveUnusedType("дфст"),

  /**
   * Второе лицо
   */
  SecondPerson("2л"),

  /**
   * Женский род
   */
  Female("жр"),

  /**
   * Винительный падеж
   */
  Accusative("вн"),

  /**
   * Будущее время
   */
  FutureTime("буд"),

  /**
   * Вопросительное наречие
   */
  InterrogativeAdverb("вопр"),

  /**
   * Некая часть речи (?)
   */
  POSL_PART_OF_SPEECH("ПОСЛ"),

  /**
   * Переходный глагол
   */
  Transive("пе"),

  /**
   * Жаргонизм
   */
  Slang("жарг"),

  /**
   * Повелительное наклонение (императив)
   */
  Imperative("пвл"),

  /**
   * Числительное (количественное)
   */
  Numeral("ЧИСЛ"),

  /**
   * Предикатив
   */
  Predicative("ПРЕДК"),

  /**
   * Частица
   */
  Particle("ЧАСТ"),

  /**
   * Предлог
   */
  Pretext("ПРЕДЛ"),

  /**
   * Звательный падеж (Отче, Боже)
   */
  Vocative("зв"),

  /**
   * Имя (Иван, Михаил)
   */
  Name("имя"),

  /**
   * Предложный падеж
   */
  Prepositional("пр"),

  /**
   * Третье лицо
   */
  ThirdPerson("3л"),

  /**
   * Инфинитив
   */
  Infinitive("ИНФИНИТИВ"),

  /**
   * Безличный глагол
   */
  InpersonalVerb("безл"),

  /**
   * Притяжательное
   */
  Superlative("притяж"),

  /**
   * Порядковое числительное
   */
  OrdinalNumber("ЧИСЛ-П"),

  /**
   * Фразеологизм
   */
  Idiom("ФРАЗ"),

  /**
   * Родительный падеж
   */
  Genitive("рд"),

  /**
   * Союз
   */
  Union("СОЮЗ");

  private final String token;

  MorphologyTag(final String token) {
    this.token = token;
  }

  @Override
  public String toString() {
    return token;
  }

  public static MorphologyTag fromString(final String token) {
    for (MorphologyTag info : values()) {
      if (info.token.equals(token)) {
        return info;
      }
    }
    throw new IllegalArgumentException("Invalid token: " + token);
  }
}
