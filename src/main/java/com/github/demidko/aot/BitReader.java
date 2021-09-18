package com.github.demidko.aot;

import static java.util.BitSet.valueOf;

import java.util.BitSet;

/**
 * Класс предназначен для побитового чтения из примитивов
 */
public class BitReader {

  private final BitSet bs;
  private int pos = -1;

  public BitReader(long... n) {
    bs = valueOf(n);
  }

  public BitReader(byte... b) {
    bs = valueOf(b);
  }

  public BitReader(BitReader other) {
    bs = (BitSet) other.bs.clone();
    pos = other.pos;
  }

  /**
   * @return метод читает следующий по порядку бит
   */
  public boolean readBit() {
    return bs.get(++pos);
  }

  /**
   * Метод читает следующие N бит по порядку
   *
   * @param size количество бит
   * @return биты сохраненные в длинное целое число
   */
  public long readLong(int size) {
    BitWriter w = new BitWriter();
    for (int i = 0; i < size; ++i) {
      w.write(readBit());
    }
    return w.toLong();
  }

  /**
   * @return прочитать следующее длинное целое
   */
  public long readLong() {
    return readLong(64);
  }

  /**
   * Метод читает следующие N бит по порядку
   *
   * @param size количество бит
   * @return биты сохраненные в целое число
   */
  public int readInt(int size) {
    return (int) readLong(size);
  }

  /**
   * @return прочитать следующее целое
   */
  public int readInt() {
    return readInt(32);
  }


  /**
   * Метод читает следующие N бит по порядку
   *
   * @param size количество бит
   * @return биты сохраненные в короткое целое число
   */
  public short readShort(int size) {
    return (short) readLong(size);
  }

  public short readShort() {
    return readShort(16);
  }

  /**
   * Метод читает следующие N бит по порядку
   *
   * @param size количество бит
   * @return биты сохраненные в байт
   */
  public byte readByte(int size) {
    return (byte) readLong(size);
  }

  public byte readByte() {
    return readByte(8);
  }
}
