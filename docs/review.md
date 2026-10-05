# Pre-publication review

The review covers analyzer production base `f1cf1b2f2004fff4dd0a6f05344e760263d900b8`
and the implementation at `c668d1713ca2f1ca1c28da23bdfd0ad08398917a`, plus bytecode
base `ba2dd3e272ad181d84e56e4726af7aa03d3c65f0` and implementation
`cb10bfd9f1aaa1a86875e2bbe4f38ac388696983`. A separate reviewer inspected production,
public-contract tests, the differential stream, benchmark source, generated JMH
loops, and raw results without modifying files or running competing benchmarks.
Review follow-ups change the harness, tests and documentation, not production code.

## Findings and resolution

No production/API regression was identified. The reviewer found a reproducibility
issue: `coldStart` and `dictionaryDigest` did not honor `-PbaselineJar`, unlike the
benchmark and fixture tasks. All diagnostic tasks now honor it, reject missing or
unrelated archives, and the digest/startup/histogram tools report the loaded code
location, Java/VM version, collectors, initial heap and maximum heap. This prevents
a missing baseline from silently falling back to candidate classes. Earlier
baseline measurements were made before production edits and are not invalidated.

Expanded tests on the original JAR found an existing invalid-ID defect:
`transformationIndex * 2` can overflow. `Integer.MIN_VALUE` aliases form zero,
although its ID and equality remain distinct. The candidate preserves these
unchanged accessors. This is documented and tested as an **observed defect**, not
a desired linguistic or API behavior. A future validation fix should be explicit.

## Representation and algorithm

The unchanged fourth dictionary block contains 3,034,914 records in 36,766,208
bytes. Each record contains a big-endian hash, a count, and ordered lemma indices.
Previously each record became an Integer key, a HashMap node and an int array.
The candidate retains those bytes and builds 4,194,304 primitive long buckets
(32 MiB): high 32 bits hold the hash, low 32 bits the record offset plus one.
Zero denotes an empty bucket, so zero and negative hash keys remain valid.

Linear probing uses a mixed hash, below 75% load. Duplicate records retain
last-key-wins behavior; duplicate references and their order stay in the original
bytes. Lookup scans referenced paradigms in the same order, reads each **current**
dictionary string, rejects unequal string hashes and then checks full equality.
This preserves hash collisions, homonym multiplicity and externally replaced
strings. There is no cached result list or copied morphological state.

IDs remain low 32-bit lemma index plus high 32-bit transformation index. Primitive
packing/unpacking replaces the bit-stream objects; the arithmetic hash formula
matches `Objects.hash(int, int)`, including signed overflow. Public signatures,
`IOException` declaration, dependencies, dictionary bytes and enum ordinals remain.

The index arrays are private final state published by class initialization and
are never mutated afterward. String's own hash caching is managed by the JDK.
Concurrent readers are covered by a four-thread test against frozen analyses.
External concurrent writes through exposed dictionary lists remain unsynchronized,
as before; the review does not claim those writes are thread-safe.

## What the differential oracle proves

The whole-dictionary SHA-256 is over a sequential, length-framed stream, not just
aggregate counts. For every string in dictionary order, it includes the string,
ordered result count, and every result's ID, hash code, spelling, ordered morphology
tags, part of speech and lemma ID. At first encounter of each lemma it includes
the entire ordered paradigm with those same fields. Every form has an ID round-trip
assertion. Duplicates are serialized repeatedly; no results or tags are sorted or
deduplicated. The visited-lemma set only avoids re-emitting a whole paradigm and
does not determine output order. All 174,628 lemmas are encountered.

The original production implementation and candidate run separately with the same
harness and resource; expected outputs are not regenerated from changed production
code. The digest is identical:
`1cb85ee3c89098b4419750230bdf2c9fe74798ea383d56bfc7c77933c2eb984a`.
It covers 3,039,129 strings, 5,017,012 analyses and 5,017,012 paradigm forms.
The 579 checked-in case digests and 67 readable analyses add local failure
attribution. These are independent **software-version** comparisons, not an
independent linguistic gold standard. Unknown input, mutated output and invalid ID
behavior require separate tests and are not implied by the full-dictionary hash.

## Difficult cases covered

| Category | Concrete cases and checks |
| --- | --- |
| Ambiguity and repeated forms | `замок` (five ordered distinct IDs; 24 paradigm entries with repeated spellings), `стали`, `пила`, `печь`, `косой`, `атлас`, `мука`, `вести`, `мой`, `три`, `сорок` |
| Suppletion and inflection | `люди` → `человек`, `детей`, `ребенку`, `шла`, `идти`; complete ordered paradigms and inverse lookup of their forms |
| Verbs and participles | `читать`, `любить`, `спать`, `бежавший`, `прочитанный`, `прочитав`, `съешь`; tags and transitivity included in frozen observations |
| Gender and animacy | `убийца`, `сирота`, `коллега`, `судья`, `кот`, `кошка`, `стол`; combined common-gender and animacy tags retained |
| Other morphology | `красивее`, `лучший`, `красивыми`, `моя`, `его`, `пальто`, `ножницы`, `двое`, `пять`, `первый`, `быстро`, `нельзя`, `увы`, `и`, `в` |
| Names and compounds | `Иванов`, `Мария`, `Санкт-Петербург`, `Ростов-на-Дону`, `кто-нибудь`, `из-за`, `по-русски`, `пол-яблока`, `1-й`; current presence/absence is observed, not certified as correct |
| Normalization and Unicode | Mixed case, `ё/е`, ROOT/US/Turkish default locales, decomposed ё, combining stress, mixed Latin/Cyrillic, isolated high/low surrogates, emoji, NBSP, bidi and zero-width characters; no trimming/tokenization assumed |
| Unknowns | Missing `аватар`/`автозаказ` recorded as current vocabulary limitations; 75 additional frozen unknown queries span misspellings, Latin, identifiers, punctuation, mixed scripts and Unicode |
| IDs | Concrete frozen IDs; legacy `bits` oracle on 100 boundary pairs and 4,096 seeded random longs; negative/out-of-range indices; valid last form, first invalid form and overflow aliases |
| Mutable outputs | Lookup-list clearing is local; transformation-list replacement is local; morphology and dictionary string replacements are shared; same-hash string replacement affects lookup; edits are restored |
| Concurrency and binary/index edges | 2,144 concurrent checks against frozen analyses; endianness, empty records, duplicate/signed/zero keys, collisions, truncation, and a deterministic 10,000-record independent-map comparison |

The separate bytecode suite freezes all 71 ordinal/name/token triples, checks all
256 byte decodings and 65,536 UTF-16 encoding inputs against an independent
restricted-CP1251 expectation, and checks invalid/null diagnostics and POS selection.
No new dictionary, importer or compiler behavior is introduced.

## Benchmark interpretation

The original ID benchmark varies across 1,024 runtime-populated meanings and the
generated JMH loop consumes every returned long. No constant-folding or DCE defect
was found. Its approximately 1.3 ns/op result is hot-accessor throughput expressed
as time/op, not isolated latency, persistence or end-to-end application speed.
Additional review benchmarks use 8,192 seeded random meaning IDs and a variant
that prevents helper inlining. Each long is returned to JMH consumption.

The first unknown benchmark repeats one token and one probe path. Its 84.32 to
87.00 ns/op mean difference has substantially overlapping 99.9% intervals
(79.77–88.88 versus 84.26–89.73 ns), so it does not establish a general regression.
The review corpus has 61 known queries and 75 unknowns: 24 misspellings, 12 Latin
strings, 12 identifiers, 11 punctuated/spaced inputs, eight mixed-script inputs and
eight Unicode cases. These are authored workload strata, not measured production
frequencies. Seeded 1,000-input sequences have exact 0%, 10%, 50% or 100% miss rates.
Each operation constructs a new token from a new char array, avoiding dictionary
string identity and cached query hashes. Both versions include that construction
cost. Counts were frozen by the original JAR and checked at benchmark setup.

Tag parsing must report both paths: reused tokens 53.40 → 5.67 ns/op; fresh tokens,
including construction, 63.90 → 27.03 ns/op. The former is not general parser or
compiler speedup. The compiler still pins its older bytecode API.

Historical timing JSON agrees on JVM executable/version, 1 GiB fixed heap, thread,
fork, warmup and measurement settings. It did not record actual collector names.
Review startup runs explicitly report collectors and heap settings, alternate old
and new versions, and load **both from JARs** so packaging does not confound startup.
Used heap follows an explicit GC request; it is not RSS or a heap-dump attribution.
Class histograms separately verify the disappearance of millions of boxed keys,
map nodes and small arrays. JVM startup itself and cold OS disk caches are excluded.

## Review measurement results

All timings are ns/op (mean ± JMH 99.9% confidence interval); B/op includes token
construction for the fresh-query workloads. Baseline/candidate JSON agrees on JVM,
VM/JDK version, arguments, thread, forks and warmup/measurement configuration.

| Workload | Baseline ns/op | Candidate ns/op | Baseline B/op | Candidate B/op |
| --- | ---: | ---: | ---: | ---: |
| encodeShuffled | 203.66 ± 7.09 | 1.90 ± 0.06 | 167.69 | 0.00 |
| encodeShuffledNoInline | 206.46 ± 5.91 | 2.80 ± 0.07 | 167.69 | 0.00 |
| Fresh query, 0% misses | 228.35 ± 10.38 | 194.72 ± 10.67 | 263.71 | 247.71 |
| Fresh query, 10% misses | 239.10 ± 7.79 | 181.04 ± 5.05 | 250.37 | 234.37 |
| Fresh query, 50% misses | 145.79 ± 16.61 | 130.78 ± 4.86 | 196.68 | 180.68 |
| Fresh query, 100% misses | 52.96 ± 3.29 | 55.83 ± 3.56 | 128.57 | 112.57 |

Both ID setups produce 8,184 distinct IDs among 8,192 inputs and the same ordered
checksum (`-6141685813442180361`). Results remain observable with helper inlining
disabled, supporting the primitive-operation explanation rather than constant
folding. The 0% and 10% miss workloads improve with disjoint reported intervals.
The 50% miss intervals overlap; its lower candidate mean is not a confirmed gain
at this confidence level. At 100% misses the mean rises by 2.87 ns (5.4%), also with
overlapping intervals (49.67–56.25 versus 52.27–59.39 ns). Do not describe either
the single-token or varied-unknown result as an established general regression.
All fresh-query cases save about 16 B/op. Deployment traffic frequencies and
streaming working sets beyond these queries remain unmeasured.

Baseline first-lookup times (ms): 1137.284, 1076.613, 1266.839, 1302.911, 1120.616.

Optimized first-lookup times (ms): 1129.340, 1114.792, 1240.653, 1243.847, 1153.960.

Five alternating JAR/JAR fresh-JVM runs (medians, decimal MB):

| Measurement | Baseline | Candidate |
| --- | ---: | ---: |
| First lookup, ms | 1137.28 | 1153.96 |
| Thread allocation, MB | 852.52 | 626.05 |
| Post-GC used heap, MB | 494.47 | 330.35 |

The memory reduction repeats with matched packaging, G1 collectors, JDK and heap
settings. Timing ranges overlap; no startup-latency improvement is claimed.
