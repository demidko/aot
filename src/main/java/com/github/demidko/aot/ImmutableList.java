package com.github.demidko.aot;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Класс позволяет создать список меньшего размера, на основе массива большего размера, без копирования.
 */
class ImmutableList<T> implements List<T> {

  class Iter implements Iterator<T> {

    private int i = -1;

    @Override
    public boolean hasNext() {
      return (i + 1) < len;
    }

    @Override
    public T next() {
      return arr[++i];
    }
  }

  private final T[] arr;
  private final int len;

  ImmutableList(T[] arr, int len) {
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
  public Iterator<T> iterator() {
    return new Iter();
  }

  @Override
  public Object[] toArray() {
    return arr;
  }

  @Override
  public <K> K[] toArray(K[] ts) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean add(T T) {
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
  public boolean addAll(Collection<? extends T> collection) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(int i, Collection<? extends T> collection) {
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
  public T get(int i) {
    return arr[i];
  }

  @Override
  public T set(int i, T T) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void add(int i, T T) {
    throw new UnsupportedOperationException();
  }

  @Override
  public T remove(int i) {
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
  public ListIterator<T> listIterator() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ListIterator<T> listIterator(int i) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<T> subList(int i, int i1) {
    throw new UnsupportedOperationException();
  }
}