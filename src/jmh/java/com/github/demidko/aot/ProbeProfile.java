package com.github.demidko.aot;

import java.io.DataInputStream;
import java.util.zip.GZIPInputStream;

/** Structural probe counts, independent of JIT timing. No analyzer initialization. */
public class ProbeProfile {
  private static int integer(byte[] data, int offset) {
    return (data[offset] & 255) << 24 | (data[offset + 1] & 255) << 16
        | (data[offset + 2] & 255) << 8 | data[offset + 3] & 255;
  }
  private static int mix(int hash, boolean avalanche) {
    hash ^= hash >>> 16;
    if (avalanche) {
      hash *= 0x7feb352d;
      hash ^= hash >>> 15;
      hash *= 0x846ca68b;
      hash ^= hash >>> 16;
    }
    return hash;
  }
  public static void main(String[] args) throws Exception {
    ByteBlock refs;
    try (DataInputStream in = new DataInputStream(new GZIPInputStream(ProbeProfile.class.getResourceAsStream("/mrd.gz")))) {
      for (int i = 0; i < 3; i++) ByteBlock.readBlockFrom(in);
      refs = ByteBlock.readBlockFrom(in);
    }
    for (boolean avalanche : new boolean[]{false, true}) {
      int capacity = 1;
      while (capacity < (long)refs.getLinesCount() * 4 / 3 + 1) capacity <<= 1;
      long[] buckets = new long[capacity];
      long total = 0;
      int max = 0;
      int[] histogram = new int[capacity];
      byte[] records = refs.getBytes();
      for (int i = 0, offset = 0; i < refs.getLinesCount(); i++) {
        int hash = integer(records, offset);
        int slot = mix(hash, avalanche) & (capacity - 1);
        int probes = 1;
        while (buckets[slot] != 0 && (int)(buckets[slot] >>> 32) != hash) {
          slot = (slot + 1) & (capacity - 1); probes++;
        }
        buckets[slot] = ((long)hash << 32) | (offset + 1L);
        total += probes; max = Math.max(max, probes); histogram[probes]++;
        offset += 8 + integer(records, offset + 4) * 4;
      }
      int cumulative = 0, p99 = 0;
      for (int i = 0; i < histogram.length; i++) {
        cumulative += histogram[i];
        if (cumulative >= refs.getLinesCount() * .99) { p99 = i; break; }
      }
      for (String word : new String[]{"елка", "стали", "несуществующееслово123"}) {
        int hash = word.hashCode(), slot = mix(hash, avalanche) & (capacity - 1), probes = 1;
        while (buckets[slot] != 0 && (int)(buckets[slot] >>> 32) != hash) {
          slot = (slot + 1) & (capacity - 1); probes++;
        }
        System.out.printf("avalanche=%s word=%s probes=%d%n", avalanche, word, probes);
      }
      System.out.printf("avalanche=%s records=%d slots=%d mean_probes=%.3f p99_probes=%d max_probes=%d%n", avalanche, refs.getLinesCount(), capacity, (double)total / refs.getLinesCount(), p99, max);
    }
  }
}
