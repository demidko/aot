package com.github.demidko.aot;

import org.junit.jupiter.api.Test;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;
import static com.github.demidko.aot.morphology.MorphologyTag.*;

class AotReaderTest {
  private ByteBlock block(int count, byte[] bytes) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    DataOutputStream data = new DataOutputStream(out);
    data.writeInt(count); data.writeInt(bytes.length); data.write(bytes);
    return ByteBlock.readBlockFrom(new DataInputStream(new ByteArrayInputStream(out.toByteArray())));
  }
  @Test void stringsKeepEmptyLinesAndCp1251Characters() throws Exception {
    assertArrayEquals(new String[]{"", "я-0ё"}, AotReader.readStrings(block(2,
        new byte[]{100, (byte)255, 45, 48, (byte)184, 100})));
  }
  @Test void stringsKeepTheirContentsAcrossScratchBufferGrowthAndReuse() throws Exception {
    String[] expected = {"я".repeat(40), "кот", "", "е".repeat(60), "кот"};
    String wire = String.join("d", expected) + "d";
    String[] actual = AotReader.readStrings(block(expected.length,
        wire.getBytes(java.nio.charset.Charset.forName("windows-1251"))));
    assertArrayEquals(expected, actual);
    assertNotSame(actual[1], actual[4]);
  }
  @Test void morphologyKeepsOrderAndDuplicates() throws Exception {
    assertArrayEquals(new Object[]{new com.github.demidko.aot.morphology.MorphologyTag[]{Noun, Genitive, Noun}, new com.github.demidko.aot.morphology.MorphologyTag[0]},
        AotReader.readMorph(block(2, new byte[]{(byte)Noun.ordinal(), (byte)Genitive.ordinal(), (byte)Noun.ordinal(), 100, 100})));
  }
  @Test void lemmasAndSignedHashKeysUseBigEndian() throws Exception {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(bytes);
    out.writeInt(2); out.writeInt(0x12345678); out.writeInt(256); out.writeInt(17); out.writeInt(3);
    assertArrayEquals(new int[]{0x12345678, 256, 17, 3}, AotReader.readLemmas(block(1, bytes.toByteArray()))[0]);
    bytes.reset();
    out.writeInt(Integer.MIN_VALUE); out.writeInt(3); out.writeInt(7); out.writeInt(2); out.writeInt(7);
    WordformReferences refs = AotReader.readRefs(block(1, bytes.toByteArray()));
    int offset = refs.find(Integer.MIN_VALUE);
    assertEquals(3, refs.size(offset));
    assertArrayEquals(new int[]{7, 2, 7}, new int[]{refs.lemmaId(offset, 0), refs.lemmaId(offset, 1), refs.lemmaId(offset, 2)});
  }
  @Test void referenceCollisionsZeroAndDuplicateKeys() throws Exception {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(bytes);
    // All these keys start in bucket zero in an eight-bucket table.
    for (int key : new int[]{0, 1, 18, 0}) {
      out.writeInt(key); out.writeInt(1); out.writeInt(bytes.size());
    }
    WordformReferences refs = AotReader.readRefs(block(4, bytes.toByteArray()));
    assertEquals(44, refs.lemmaId(refs.find(0), 0));
    assertEquals(20, refs.lemmaId(refs.find(1), 0));
    assertEquals(32, refs.lemmaId(refs.find(18), 0));
    assertEquals(-1, refs.find(21));
    assertEquals(-1, AotReader.readRefs(block(0, new byte[0])).find(0));
  }
  @Test void packedReferenceIndexMatchesIndependentMap() throws Exception {
    java.util.Random random = new java.util.Random(719);
    java.util.Map<Integer, int[]> expected = new java.util.HashMap<>();
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(bytes);
    for (int i = 0; i < 10000; i++) {
      int key = i % 3 == 0 ? i % 17 : random.nextInt();
      int[] values = new int[random.nextInt(5)];
      out.writeInt(key); out.writeInt(values.length);
      for (int j = 0; j < values.length; j++) { values[j] = random.nextInt(); out.writeInt(values[j]); }
      expected.put(key, values);
    }
    WordformReferences refs = AotReader.readRefs(block(10000, bytes.toByteArray()));
    for (java.util.Map.Entry<Integer, int[]> entry : expected.entrySet()) {
      int offset = refs.find(entry.getKey());
      assertTrue(offset >= 0);
      assertEquals(entry.getValue().length, refs.size(offset));
      for (int j = 0; j < entry.getValue().length; j++) assertEquals(entry.getValue()[j], refs.lemmaId(offset, j));
    }
    for (int i = 0; i < 10000; i++) {
      int key = random.nextInt();
      assertEquals(expected.containsKey(key), refs.find(key) >= 0);
    }
  }
  @Test void truncatedBlocksFail() {
    assertThrows(EOFException.class, () -> ByteBlock.readBlockFrom(new DataInputStream(new ByteArrayInputStream(new byte[7]))));
  }
}
