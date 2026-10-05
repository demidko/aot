## Summary

Reduce the analyzer's retained heap and lookup cost while preserving its dictionary and public behavior. Retain packed reference records and index their offsets instead of expanding them into millions of boxed map entries.

## Changes

- Use primitive hash/offset buckets with measured hash mixing; preserve reference order, duplicates, and full string collision checks.
- Filter candidate forms by their string hashes before comparing contents, while keeping edits through shared dictionary lists visible.
- Replace bit-stream ID serialization and boxed hash-code calculation with equivalent primitive operations.
- Add frozen compatibility cases, an exhaustive dictionary differential, reproducible benchmarks, and startup/heap diagnostics.
- Provide English and Russian READMEs with accurate API, ID, and dictionary-provenance limitations.

## Validation and independent review

- Characterization tests passed and were committed before production changes. The source baseline is `f1cf1b2f2004fff4dd0a6f05344e760263d900b8`.
- Final analyzer build: **19 tests pass**. All **12 public compatibility tests also pass against the original JAR**.
- 579 frozen cases and 67 readable analyses cover ambiguity, duplicates, suppletion, inflection, participles, common gender, animacy/transitivity, names, compounds, normalization, and malformed inputs.
- The whole-dictionary SHA-256 matches across **3,039,129 strings, 5,017,012 analyses, 174,628 lemmas, and 5,017,012 paradigm forms**. It compares actual ordered IDs, spellings, tags, parts of speech and complete paradigms, not merely totals.
- Additional checks cover 4,196 boundary/random IDs against the unchanged `bits` implementation, invalid-index behavior, shared-list mutation, concurrent frozen-output reads, and reference-index collisions.
- A separate reviewer found no production/API regression. A diagnostic baseline-selection issue was fixed and verified; missing or unrelated JARs now fail explicitly.
- Public analyzer signatures and dictionary bytes are unchanged.

The full review, case matrix, raw data and commands are in [docs/review.md](https://github.com/fluffy-manul/aot/blob/characterize-and-optimize/docs/review.md).

## Performance

JMH 1.37, three forks, three 1-second warmups and five 1-second measurements per fork. Timings below are means; `±` denotes the 99.9% confidence interval. Fresh-query measurements include token construction and avoid dictionary-string identity. Startup/heap figures are medians of five alternating JAR/JAR runs with the same JDK, G1 collectors, and fixed 1 GiB heap.

| Measurement | Baseline | Candidate |
| --- | ---: | ---: |
| Fresh known queries | 228.35 ± 10.38 ns/op | 194.72 ± 10.67 ns/op |
| Fresh queries, 10% misses | 239.10 ± 7.79 ns/op | 181.04 ± 5.05 ns/op |
| Fresh queries, 100% misses | 52.96 ± 3.29 ns/op | 55.83 ± 3.56 ns/op |
| Randomized ID encoding | 203.66 ± 7.09 ns/op | 1.90 ± 0.06 ns/op |
| Post-GC used heap | 494.47 MB | 330.35 MB |
| Startup thread allocation | 852.52 MB | 626.05 MB |
| First lookup | 1,137.28 ms | 1,153.96 ms |

The ID result also remains fast with helper inlining disabled: 206.46 → 2.80 ns/op. Generated JMH loops consume varying results; these are hot-accessor microbenchmarks, not end-to-end persistence measurements.

## Limitations

The authored workloads are not production-frequency estimates. The 100%-miss intervals overlap, so its higher candidate mean does not establish a general unknown-word regression; all fresh-query cases save about 16 B/op. Startup latency is comparable, with no demonstrated gain. Stronger mixing improves average probe counts but increases the longest probe chain.

Snapshots record observed behavior, not linguistic correctness. IDs remain dictionary-relative, and shared dictionary lists remain mutable and unsynchronized. Existing invalid transformation-index overflow can alias a valid form; this is documented as a retained defect, not recommended behavior. Dictionary licensing/provenance remains a separate review topic. This PR does not migrate dictionary data or reorder enums.
