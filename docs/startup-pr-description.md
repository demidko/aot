Loading the dictionary currently creates a temporary `char[]` for every flexion string, then copies it into a `String`. Reuse a growing scratch buffer and copy only each string's range; increase the gzip input buffer from 512 bytes to 64 KiB. The dictionary, lookup/index code, IDs, ordering and public mutable backing arrays stay unchanged.

Five alternating fresh-JVM runs on Temurin 21/G1 with a fixed 1 GiB heap reduced startup thread allocation from **626,294,456 to 499,471,160 bytes (20.2%)** on the published dictionary. Median first lookup decreased from 1,146.306 to 1,093.495 ms, but timing ranges overlap: this does **not** establish a guaranteed startup speedup. Retained heap was unchanged. Peak RSS varies with the workload: the published dictionary's median rose 2.3% (508,668→520,264 KiB), while a private larger-resource experiment fell 5.2%. That experiment's corpus and binaries are not included.

Validation:

- **20 tests pass**, including long→short→empty→long buffer reuse, distinct equal strings, and existing mutation/aliasing and malformed-ID behavior.
- Full public dictionary digest unchanged: `1cb85ee3c89098b4419750230bdf2c9fe74798ea383d56bfc7c77933c2eb984a` (3,039,129 surfaces; 5,017,012 analyses).
- Dictionary SHA-256 unchanged: `7bc652a568090d6f07e9d53213329769e4d1597a5688d9c77e422b63b5c5fc41`.
- A three-fork warmed check on the private larger-resource experiment found overlapping timing intervals and unchanged 210.1253 B/op at 50% misses; no warmed speedup is claimed.
- Independent review found no semantic issue in the changes. See [STARTUP.md](https://github.com/fluffy-manul/aot/blob/reduce-dictionary-startup-allocations/STARTUP.md) for measurements and limitations.

This PR does not migrate dictionary data or choose an ID/tag policy.
