# Startup allocation experiment

Baseline: merged optimized analyzer
`b47f9ab3d4584bd483894c29af3c840f842d32d5`, Temurin 21.0.12.1, G1, fixed 1 GiB heap.
Published dictionary SHA-256 remains
`7bc652a568090d6f07e9d53213329769e4d1597a5688d9c77e422b63b5c5fc41`.

The reader now reuses a scratch char[] when decoding strings. Each String copies
only its own characters, preserving independent values and existing shared
public arrays. The gzip input buffer grows from 512 bytes to 64 KiB. Lookup,
reference indexing, tag order, IDs and resource data are unchanged.

Five alternating fresh-JVM measurements per variant:

| Published dictionary | Baseline | Candidate |
| --- | ---: | ---: |
| First lookup median | 1,146.306 ms | 1,093.495 ms |
| First lookup range | 1,114–1,257 ms | 1,083–1,199 ms |
| Startup thread allocation | 626,294,456 B | 499,471,160 B |
| Retained heap median | 330,712,528 B | 330,640,632 B |
| Maximum RSS median | 508,668 KiB | 520,264 KiB |

Allocation drops 20.2%. Timing ranges overlap; the median decrease is preliminary,
not a guaranteed speedup. Retained memory does not materially change, and RSS
increases about 2.3%. An isolated newer-resource experiment similarly reduces
startup allocation about 19.4% and median startup about 6.4%, but does not solve
its roughly 40% retained-heap growth. That resource is neither included nor
installed. Dictionary migration remains a separate decision.

Validation: 20 tests pass, including long→short→empty→long string decoding and
distinct equal String objects; existing public mutation/aliasing tests pass.
Full public dictionary digest is unchanged:
`1cb85ee3c89098b4419750230bdf2c9fe74798ea383d56bfc7c77933c2eb984a`.
An independent read-only review found no semantic issue in the changes.

Reproduction uses the isolated aot-compiler `tools/assessment/run_startup.py`
harness with five alternating rounds, one fresh JVM per observation, no
concurrent build/corpus processing. Raw logs and the fuller retained-structure
assessment accompany that separate code-only tooling branch. The new corpus
and experimental resources remain private and are not part of this change.

A warmed shared-corpus check on the new-resource experiment (50% misses,
fresh Strings, three forks) measured 329.48 ± 19.41 ns/op baseline versus
318.36 ± 13.31 ns/op candidate, with 210.1253 B/op for both. Intervals overlap;
this supports no detected regression on that workload, not a warmed speedup.
