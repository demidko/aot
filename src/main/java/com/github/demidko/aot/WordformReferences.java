package com.github.demidko.aot;

/**
 * Hash-to-lemma references backed by the dictionary's original big-endian records.
 * Each bucket packs a hash and a record offset plus one (zero means empty). Keeping records
 * packed avoids a boxed key, map node, and small int array for every word hash.
 * This index only identifies candidate lemmas; callers must still compare strings.
 */
final class WordformReferences {
  private final byte[] records;
  private final long[] buckets;

  WordformReferences(ByteBlock block) {
    records = block.getBytes();
    long needed = (long) block.getLinesCount() * 4 / 3 + 1;
    if (needed > (1 << 30)) throw new IllegalArgumentException("Too many reference records");
    int capacity = 1;
    while (capacity < needed) capacity <<= 1;
    buckets = new long[capacity];
    for (int i = 0, offset = 0; i < block.getLinesCount(); i++) {
      // Last record wins for duplicate keys, matching the previous HashMap reader.
      int hash = integer(offset);
      buckets[slot(hash)] = ((long) hash << 32) | (offset + 1L);
      offset += 8 + size(offset) * 4;
    }
  }

  private int slot(int hash) {
    int mask = buckets.length - 1;
    // Mix all hash bits before probing: dictionary suffix patterns otherwise
    // create clusters in adjacent buckets (see docs/probe-profile.txt).
    int mixed = hash ^ (hash >>> 16);
    mixed *= 0x7feb352d;
    mixed ^= mixed >>> 15;
    mixed *= 0x846ca68b;
    mixed ^= mixed >>> 16;
    int slot = mixed & mask;
    while (buckets[slot] != 0 && (int) (buckets[slot] >>> 32) != hash) {
      slot = (slot + 1) & mask;
    }
    return slot;
  }

  /** Record offset, or -1 when the hash is absent. */
  int find(int hash) {
    return (int) buckets[slot(hash)] - 1;
  }

  int size(int offset) {
    return integer(offset + 4);
  }

  int lemmaId(int offset, int index) {
    return integer(offset + 8 + index * 4);
  }

  private int integer(int offset) {
    return (records[offset] & 255) << 24
        | (records[offset + 1] & 255) << 16
        | (records[offset + 2] & 255) << 8
        | records[offset + 3] & 255;
  }
}
