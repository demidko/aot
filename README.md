# Aot: Russian morphological analysis for Java

[Русская версия](README.ru.md)

Aot is a dictionary-based Russian morphological analyzer. It finds lemmas,
morphological tags, and inflected forms, and returns multiple analyses when a word
is ambiguous. It does not choose an interpretation from sentence context.

The project reworks [aot-lematizer](https://github.com/bazhenov/aot-lematizer) with a
simpler API and build, without its closed-source dependencies.

## Installation

Add [JitPack](https://jitpack.io/#demidko/aot) to your repositories and select a
published version. For example, in Gradle Kotlin DSL:

```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}
dependencies {
    implementation("com.github.demidko:aot:2025.11.25")
}
```

The source uses Java 8-compatible language features. Build this checkout with a
JDK supported by its Gradle wrapper; the tests and benchmarks use JDK 21. The Java
version required by a published JitPack artifact also depends on its build JDK.

## Usage

```java
import com.github.demidko.aot.WordformMeaning;
import java.util.List;

class Example {
    public static void main(String[] args) {
        List<WordformMeaning> meanings = WordformMeaning.lookupForMeanings("люди");
        for (WordformMeaning meaning : meanings) {
            System.out.println(meaning.getLemma());      // человек
            System.out.println(meaning.getMorphology()); // dictionary tags
            System.out.println(meaning.getPartOfSpeech());
            for (WordformMeaning form : meaning.getTransformations()) {
                System.out.println(form + " " + form.getMorphology());
            }
        }
    }
}
```

`getTransformations()` returns the complete dictionary paradigm. To inflect a word,
filter those forms by the morphology tags you need; there is no separate inflection
method. A lemma is the dictionary's base form, such as an infinitive for a verb,
not necessarily a first-person singular form.

`замок`, for example, has five analyses in the bundled dictionary. Different
analyses can share the same spelling and tags. Preserve their identities and
multiplicity if your application needs ambiguity information; the library does
not attach definitions that distinguish a castle from a lock.

## Input and identity

- Lookup lowercases input using the JVM's default locale and treats `ё` as `е`.
- Pass a single word. Lookup does not trim whitespace, tokenize text, remove
  accents, or normalize combining characters. Unknown words return an empty list;
  `null` throws `NullPointerException`.
- `getId()` and `lookupForMeaning(long)` round-trip a meaning. IDs contain dictionary
  indices and are only stable for the same dictionary ordering. Store the dictionary
  version with persisted IDs. Invalid IDs are not validated; some throw on access,
  while extreme transformation indices can wrap and alias a valid form.
- Result order and repeated forms reflect the dictionary, not a relevance ranking.
- Treat `listAllFlexions()` and `getMorphology()` results as read-only: their mutable
  fixed-size lists expose shared dictionary arrays.

The dictionary loads on first use and remains in memory. Account for initialization
latency and heap use when deploying the library.

## Dictionary and provenance

Words and morphology come from [morph_dict](https://github.com/sokirko74/morph_dict).
The [aot-compiler](https://github.com/demidko/aot-compiler) project converts historical
MRD/TAB files into the bundled `mrd.gz`; [aot-bytecode](https://github.com/demidko/aot-bytecode)
provides the shared tags and encoding. Historical format documentation is in
[AOT's Morph_UNIX.txt](https://github.com/sokirko74/aot/blob/master/Docs/Morph_UNIX.txt).
The upstream repository's current layout may differ from those historical inputs.

The bundled binary was updated on November 24, 2025 with corrections credited to
@danil-kondr2016. That date is not a vocabulary revision guarantee. See the
[baseline inventory](docs/characterization.md) for exact commits, checksums, known
behavior, and provenance limits. The Java project has an [MIT license](LICENSE);
consult upstream sources for dictionary licensing rather than assuming the code
license establishes the imported data's terms.

## Development

```sh
bash gradlew test
```

The [characterization suite](docs/characterization.md) separates observed behavior
from linguistic correctness. [Benchmark instructions and results](docs/benchmarks.md)
cover warmed lookup, ID operations, allocations, and first-use initialization.
