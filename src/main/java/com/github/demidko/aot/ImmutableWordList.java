package com.github.demidko.aot;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Чтобы не копировать лишний раз массивы
 */
class ImmutableWordList implements List<Word> {

  class Iter implements Iterator<Word> {

    private int i = -1;

    @Override
    public boolean hasNext() {
      return (i + 1) < len;
    }

    @Override
    public Word next() {
      return arr[++i];
    }
  }

  private final Word[] arr;
  private final int len;

  ImmutableWordList(Word[] arr, int len) {
    this.arr = arr;
    this.len = len;
  }

  @Override
  public int size() {
    return len;
  }

  @Override
  public boolean isEmpty() {
    return len == 0;
  }

  @Override
  public boolean contains(Object o) {
    return indexOf(o) != -1;
  }

  @Override
  public Iterator<Word> iterator() {
    return new Iter();
  }

  @Override
  public Object[] toArray() {
    return arr;
  }

  @Override
  public <T> T[] toArray(T[] ts) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean add(Word word) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsAll(Collection<?> collection) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(Collection<? extends Word> collection) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(int i, Collection<? extends Word> collection) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection<?> collection) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(Collection<?> collection) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Word get(int i) {
    return arr[i];
  }

  @Override
  public Word set(int i, Word word) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void add(int i, Word word) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Word remove(int i) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int indexOf(Object o) {
    for (int i = 0; i < len; ++i) {
      if (arr[i] == o) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public int lastIndexOf(Object o) {
    int res = -1;
    for (int i = 0; i < len; ++i) {
      if (arr[i] == o) {
        res = i;
      }
    }
    return res;
  }

  @Override
  public ListIterator<Word> listIterator() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ListIterator<Word> listIterator(int i) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Word> subList(int i, int i1) {
    throw new UnsupportedOperationException();
  }
}