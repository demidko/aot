# Compatibility baseline

Production code and dictionary are unchanged in the characterization commit.

| Component | Pinned commit | Role |
| --- | --- | --- |
| aot | `f1cf1b2f2004fff4dd0a6f05344e760263d900b8` | Analyzer and bundled dictionary |
| aot-bytecode 2025.02.15 | `ba2dd3e272ad181d84e56e4726af7aa03d3c65f0` | Public tags, parts of speech, byte encoding |
| aot-bytecode 2021.10.26 (compiler only) | `7740e529e1c588dcd58f431f4982070c9bd448dc` | Historical compiler format/API dependency |
| bits 2022.08.06 | `465826d1ebe418ba1f6439bb8f120893432d0821` | ID serialization |
| aot-compiler HEAD inspected | `12bb2a3cb113d777d1c3a8a95578e73fae2d650e` | Offline dictionary generation; depends on aot-bytecode **2021.10.26** |

No repository AGENTS.md or .agents/skills instructions were present in these clones.
All four projects include MIT code licenses. This does not establish the license
of the imported dictionary data. The README attributes data to
[sokirko74/morph_dict](https://github.com/sokirko74/morph_dict) and the format to
[AOT Morph_UNIX](https://github.com/sokirko74/aot/blob/master/Docs/Morph_UNIX.txt).
The binary has no embedded source revision or licensing manifest. Do not infer its
source version from its compilation date or replace it during performance work.

Dictionary SHA-256: `7bc652a568090d6f07e9d53213329769e4d1597a5688d9c77e422b63b5c5fc41`.
The last binary update is the baseline commit (November 24, 2025), credited to
@danil-kondr2016. Compiler resources have SHA-256:

- MRD: `8e7499d2d9aa29959f3307268bf5012a9db23abc226a743fc65b91583c5e9f50`
- TAB: `f0c938917675cab2c773b2ecc458da343d7f0af2f49e23450b69342c2ada5946`

## Run

Use a JDK (tested with Temurin 21.0.12.1), then `bash gradlew test`.
Tests use the packaged dictionary; no dictionary download or compilation occurs.
The original test remains. Added checks cover ordered ambiguity, duplicate forms,
suppletion, verbs/participles, inflection paradigms, common gender, animacy,
transitivity, names, hyphens, numeral forms, capitalization, е/ё, malformed UTF-16,
combining marks, unknowns, ID reconstruction, equality, and returned collections.
Small binary fixtures separately check byte order, duplicate preservation, empty
lines, and truncated input.

`characterization.tsv` freezes the unchanged implementation's observed results for
curated inputs and 512 evenly spaced dictionary strings. Each line contains input,
analysis count, and a SHA-256 digest of **ordered** analyses, IDs, strings, morphology
lists, parts of speech, lemmas, and every transformation of each analysis. Duplicate
analyses and tag ordering are included. These are compatibility observations, not
an independent linguistic gold standard. The expected results are checked in and
never recomputed by the tests. Explicit fixture regeneration uses
`bash gradlew captureCharacterization` in a separate invocation, on the recorded
baseline only; review changes instead of regenerating to make failures disappear.

Independent assertions include five analyses of замок, distinct identities for
identical surface forms, and люди → человек. Broader linguistic validation and
upstream dictionary migration require separately reviewed MRD/TAB research.
Missing vocabulary is not asserted to be linguistically invalid.

## Observed hazards, not recommended semantics

- `lookupForMeanings(null)` throws NullPointerException. Unknown input gives an empty
  list. There is no tokenization, trimming, accent removal, or Unicode normalization.
- Lowercasing uses the default locale; ё is folded into е. This loses distinctions.
- Meaning IDs encode **two 32-bit integers**, not the Javadoc's claimed 48 bits:
  lemma index in low bits, transformation index in high bits. IDs are dictionary
  relative. Reordering/recompiling a dictionary may change their meaning.
- Arbitrary IDs are accepted. Many invalid indices fail when dereferenced, but
  extreme transformation indices can overflow during multiplication by two and
  alias a valid form. For example, `Integer.MIN_VALUE` aliases form zero. This is
  an observed defect retained for compatibility, not recommended ID semantics.
- `listAllFlexions()` and morphology lists are fixed-size, mutable array-backed
  views. Mutating them changes shared dictionary state. Tests restore every edit.
  Any future cached lookup index must account for this exposed mutability.
- Homonymy is represented by dictionary entries, sometimes repeated transformations
  in the same lemma. The API does not provide lexical definitions or disambiguation.
- The compiler's HashSet iteration influences serialized reference order. Runtime
  compatibility tests preserve the order in this exact binary, not a universal
  linguistic ranking.

For a stronger optional release check, `bash gradlew dictionaryDigest` walks **every**
dictionary string and its ordered lookup results, then every encountered lemma's
complete paradigm once. It records strings, IDs, hash codes, tag order, parts of
speech and ID round trips in a SHA-256 digest. Run on the baseline and candidate
separately and compare the final digest/counts. This is intentionally outside the
normal unit-test workload.

Separate upstream research identified the November 2025 refresh as a compiler fix
restoring lemma-level animacy/transitivity ([compiler PR #1](https://github.com/demidko/aot-compiler/pull/1)),
not a vocabulary refresh. Its raw MRD/TAB files remain the historical 2021 import.
Current upstream uses JSON and includes vocabulary additions/removals and changes
to common-gender tags. The old MRD stem and a JSON full lemma are not interchangeable.
Neither resource migration nor enum reordering belongs in this optimization.
Upstream code/data licensing needs separate review (AOT/legacy lexicon LGPL notices
and imported surname data); no claim of MIT-only dictionary licensing is made here.

`analyses.tsv` additionally exposes the curated cases in readable form:
`input<TAB>id|surface|lemmaId|lemma|tags|partOfSpeech; ...`. It was captured by
putting the original production JAR first on the generator classpath via
`-PbaselineJar=/absolute/path/to/baseline.jar`; its expected values also remain fixed.
This makes ambiguity and morphology changes reviewable without decoding hashes.

## Reproducing the original-versus-candidate checks

Build the original JAR from its source commit, then use the current harness for
both runs. These paths assume checkouts named `aot` and `aot-original`; adjust JAR
filenames if directory names differ.

```sh
git worktree add --detach ../aot-original f1cf1b2f2004fff4dd0a6f05344e760263d900b8
(cd ../aot-original && bash gradlew jar)
bash gradlew jar
bash gradlew dictionaryDigest -PbaselineJar=../aot-original/build/libs/aot-original.jar
bash gradlew dictionaryDigest -PbaselineJar=build/libs/aot.jar
bash gradlew test --tests '*CharacterizationTest' --tests '*CompatibilityBoundaryTest' -PbaselineJar=../aot-original/build/libs/aot-original.jar
bash gradlew test
```

Diagnostic output names the loaded implementation. A missing archive or an archive
without the analyzer class and dictionary is rejected rather than falling back to
the current classes. The original binary-reader tests cannot run against the new
internal index type; the public test selection above deliberately excludes those
package-private implementation tests. The original public behavior is compared
with the same frozen expectations used for the candidate. See [review findings and
coverage](review.md) for edge cases, known defects and independent-review limits.
