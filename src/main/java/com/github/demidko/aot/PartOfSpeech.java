package com.github.demidko.aot;

/**
 * Часть речи, например существительное, глагол и т. д.
 */
public enum PartOfSpeech {

    /**
     * Местоимение-существительное
     */
    Pronoun("Местоимение-существительное"),

    /**
     * Краткое прилагательное
     */
    ShortAdjective("Краткое прилагательное"),

    /**
     * Междометие
     */
    Interjection("Междометие"),

    /**
     * Наречие
     */
    Adverb("Наречие"),

    /**
     * Глагол в личной форме
     */
    Verb("Глагол в личной форме"),

    /**
     * Прилагательное
     */
    Adjective("Прилагательное"),

    /**
     * Существительное
     */
    Noun("Существительное"),

    /**
     * Вводное слово
     */
    Introduction("Вводное слово"),

    /**
     * Местоимение-предикатив
     */
    PronounPredicative("Местоимение-предикатив"),

    /**
     * Местоименное прилагательное
     */
    PronounAdjective("Местоименное прилагательное"),

    /**
     * Причастие
     */
    Participle("Причастие"),

    /**
     * Деепричастие
     */
    VerbalParticiple("Деепричастие"),

    /**
     * Краткое причастие
     */
    BriefCommunion("Краткое причастие"),

    /**
     * Некая часть речи (?)
     */
    POSL_PART_OF_SPEECH("Некая часть речи"),

    /**
     * Предикатив
     */
    Predicative("Предикатив"),

    /**
     * Частица
     */
    Particle("Частица"),

    /**
     * Числительное (количественное)
     */
    Numeral("Числительное"),

    /**
     * Предлог
     */
    Pretext("Предлог"),

    /**
     * Инфинитив
     */
    Infinitive("Инфинитив"),

    /**
     * Порядковое числительное
     */
    OrdinalNumber("Порядковое числительное"),

    /**
     * Фразеологизм
     */
    Idiom("Фразеологизм"),

    /**
     * Союз
     */
    Union("Союз");

    private final String description;

    PartOfSpeech(String description) {
        this.description = description;
    }

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

    /**
     * @return описание части речи
     */
    public String getDescription() {
        return description;
    }
}
