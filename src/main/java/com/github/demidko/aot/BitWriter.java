package com.github.demidko.aot;

import static java.util.BitSet.valueOf;

import java.util.BitSet;

/**
 * Класс предназначен для побитовой записи в примитивы
 */
class BitWriter {

  private final BitSet bs;
  private int pos = -1;

  public BitWriter(byte... b) {
    bs = valueOf(b);
  }

  public BitWriter(long... l) {
    bs = valueOf(l);
  }

  public BitWriter() {
    this(new byte[8]);
  }

  public BitWriter(BitWriter other) {
    bs = (BitSet) other.bs.clone();
    pos = other.pos;
  }

  /**
   * Метод записывает следующий по порядку бит
   */
  public void write(boolean bit) {
    bs.set(++pos, bit);
  }

  /**
   * Метод сохраняет результат записи в long
   *
   * @return результат записи
   */
  public long toLong() {
    return bs.toLongArray().length == 0 ? 0 : bs.toLongArray()[0];
  }

  public int toInt() {
    return (int) toLong();
  }

  public short toShort() {
    return (short) toLong();
  }

  public byte toByte() {
    return (byte) toLong();
  }

  /**
   * Метод записывает по порядку заданное количество бит
   *
   * @param bits набор бит
   * @param len  количество бит из набора
   */
  public void write(long bits, int len) {
    BitReader r = new BitReader(bits);
    for (int i = 0; i < len; ++i) {
      write(r.readBit());
    }
  }

  public void writeLong(long l) {
    write(l, 64);
  }

  public void writeInt(int i) {
    write(i, 32);
  }

  public void writeShort(short s) {
    write(s, 16);
  }

  public void writeByte(byte b) {
    write(b, 8);
  }
}
