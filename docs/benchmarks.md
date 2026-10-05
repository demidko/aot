# Reproducible measurements

Use `bash gradlew benchmark -PbenchmarkArgs='-prof gc -rf json -rff baseline.json'`.
JMH 1.37 runs three separate JVM forks, each with three 1-second warmups and five
1-second measurements, fixed 1 GiB heap. Results consume returned values, and the
mixed lookup/ID corpus has 1,024 evenly spaced strings from the packaged dictionary.
Keep the dictionary, JDK, heap, machine, and command identical for comparisons.
Run benchmarks without other builds competing for CPU or memory.

For startup use `bash gradlew coldStart` repeatedly in fresh processes. It measures
first lookup including class initialization, thread allocation, and used heap after
an explicit GC. OS disk caches are **not** cleared; this is a cold JVM, not cold disk.
Heap-after-GC is an approximation, not a heap-dump attribution. JMH `gc.alloc.rate.norm`
is bytes per operation; throughput can be derived as 1e9 / ns-per-operation.

Baseline environment: Temurin JDK 21.0.12.1; Linux x86_64 VM, AMD EPYC 9V74,
5 exposed CPUs. Virtualized timing is noisy; inspect confidence intervals and raw
forks. Do not turn timing into flaky unit-test thresholds.

## Design under comparison

The original `Map<Integer, int[]>` materializes roughly three million hash keys,
nodes, and tiny arrays. The candidate keeps the reference block's original bytes
and builds one `long[]` whose cells pack a hash and record offset, using linear
probing below 75% load. Hash mixing reduces clustering from dictionary suffixes.
Offsets are stored plus one so all 32-bit hash values, including zero and negative
values, remain valid. Reference order and duplicate lemma IDs stay in the original
bytes. Duplicate records overwrite earlier offsets, preserving the old reader's
last-key-wins behavior. Actual strings are still compared after candidate lookup,
including when an unknown input has the same hash as a dictionary word.

This is a different in-memory representation, not a result cache. It changes neither
the gzip resource nor its wire format. Expected lookup remains O(1) for the reference
index plus candidate-paradigm scanning; linear probing has O(n) worst-case probing.
The test suite checks synthetic colliding keys, signed keys, empty reference lists,
duplicate records, and a deterministic 10,000-record comparison with an independent
HashMap. The full-dictionary differential checks actual hash collisions as well.

Alternatives considered:

- Pre-sizing HashMap could avoid resizing, but retains its per-entry node/key/array
  overhead (the histogram attributes about 146 MB to nodes and boxed keys alone).
- A direct word-to-transformation index could eliminate paradigm scanning, but
  requires additional indexing and must preserve edits through exposed mutable
  dictionary-string lists. That is a separate API/representation design decision.
- A packed offset table reuses the existing serialization and avoids those exposed
  mutability concerns. Its real lookup/initialization tradeoff is measured here.

ID encoding/decoding now uses primitive shifts/masks for the same two 32-bit fields.
The hash-code formula exactly reproduces `Objects.hash(int, int)` without boxing or
a varargs array. Morphology loading obtains one enum-values array per block instead
of repeatedly cloning it. The bits dependency remains declared as API to avoid
silently removing a dependency that downstream applications may use.

The additional `prose` workload cycles through original, pretokenized Russian prose
covering ordinary narration and technical text, capitals, ё, numbers, and unknown
Latin/mixed tokens. It complements the uniformly sampled dictionary workload, which
weights rare forms equally and should not be presented as a natural-language
frequency distribution. Compare `prose` on the old JAR with
`bash gradlew benchmark -PbaselineJar=/absolute/path/to/baseline.jar -PbenchmarkArgs='.*prose -prof gc -rf json -rff docs/prose-baseline.json'`
and without `baselineJar` on the candidate. Both use the same benchmark harness.


The final lookup loop rejects candidate forms with unequal string hashes before
calling `String.equals`. Java String caches its own hash without an auxiliary
result cache. The code always reads the current dictionary string, so mutation
through the public array-backed list still affects lookup. A same-hash replacement
test checks this explicitly. Equal hashes still require full string equality.

`offset-only.json` records the smallest-table prototype; `hash-offset-xor.json`
records packed hash/offset cells with simple spreading; `hash-offset-mixed.json`
records stronger mixing before the candidate-string hash filter. `optimized.json`
is the full three-fork **final lookup** comparison (including unknown and prose
inputs). ID, meaning-hash, and transformation measurements use
`hash-offset-mixed.json`: those methods did not change in the final lookup step.
`hash-filter-trial.json` is a one-fork screening experiment, not the final evidence.
`probe-profile.txt` records average, p99 and maximum structural probe counts. The
stronger mix reduces mean probes but increases the longest probe chain; this is
not a claim of uniformly improved worst-case latency.

## Results on the recorded VM

Means in ns/op; ± is JMH’s 99.9% confidence interval. Allocations are bytes/op.

| Operation | Baseline ns/op | Candidate ns/op | Baseline B/op | Candidate B/op |
| --- | ---: | ---: | ---: | ---: |
| lookup | 475.83 ± 30.08 | 382.50 ± 19.40 | 124.83 | 119.49 |
| prose | 187.50 ± 7.73 | 143.56 ± 9.63 | 130.67 | 130.67 |
| ambiguous | 188.55 ± 9.47 | 178.12 ± 2.79 | 229.33 | 224.00 |
| normalized | 85.75 ± 7.15 | 79.53 ± 2.85 | 210.67 | 200.00 |
| unknown | 84.32 ± 4.56 | 87.00 ± 2.73 | 16.00 | 0.00 |
| encodeId | 158.47 ± 10.57 | 1.29 ± 0.03 | 167.58 | 0.00 |
| decodeId | 234.78 ± 11.91 | 2.49 ± 0.09 | 215.58 | 24.00 |
| meaningHash | 6.61 ± 0.29 | 1.31 ± 0.08 | 40.05 | 0.00 |
| transformations | 158.96 ± 9.15 | 145.34 ± 4.71 | 1425.06 | 1425.06 |

Five fresh JVM runs per version (medians; MB = 1,000,000 bytes):

| Measurement | Baseline | Candidate |
| --- | ---: | ---: |
| First lookup, ms | 989.50 | 1001.88 |
| Thread allocation, MB | 852.51 | 626.04 |
| Retained heap after GC, MB | 494.43 | 330.37 |

The candidate reduces retained heap by about 33% and startup allocation by 27%.
Startup latency is comparable, not a demonstrated improvement. Mixed dictionary
lookup takes about 20% less time (about 24% greater reciprocal throughput). Unknown
lookup is slightly slower in this run while eliminating its boxed-key allocation.
These are single-machine measurements, not guarantees for every deployment.

## Review measurements

[The pre-publication review](review.md) audits observable JMH consumption and input
bias, and adds randomized-ID and distributed fresh-query workloads. Reproduce them
with the same harness against each JAR:

```sh
bash gradlew benchmark -PbaselineJar=../aot-original/build/libs/aot-original.jar -PbenchmarkArgs='.*ReviewBenchmark.* -prof gc -rf json -rff docs/review-baseline.json'
bash gradlew benchmark -PbaselineJar=build/libs/aot.jar -PbenchmarkArgs='.*ReviewBenchmark.* -prof gc -rf json -rff docs/review-optimized.json'
bash gradlew coldStart -PbaselineJar=../aot-original/build/libs/aot-original.jar
bash gradlew coldStart -PbaselineJar=build/libs/aot.jar
```

For startup, compare JAR with JAR or classes directory with classes directory.
Do not infer an algorithm startup gain from different resource packaging. The
review records five alternating fresh-JVM pairs with both implementations loaded
from JARs, actual collector names, and identical 1 GiB initial/maximum heap.
