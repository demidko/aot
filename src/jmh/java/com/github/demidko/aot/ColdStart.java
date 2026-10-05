package com.github.demidko.aot;

import java.lang.management.ManagementFactory;

/** Fresh JVM class initialization (OS page cache is not flushed). */
public class ColdStart {
  public static void main(String[] args) throws Exception {
    com.sun.management.ThreadMXBean bean = (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
    long thread = Thread.currentThread().getId();
    long allocated = bean.getThreadAllocatedBytes(thread);
    long start = System.nanoTime();
    int count = WordformMeaning.lookupForMeanings("замок").size();
    long elapsed = System.nanoTime() - start;
    long bytes = bean.getThreadAllocatedBytes(thread) - allocated;
    System.gc();
    long heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
    System.out.printf("first_lookup_ms=%.3f thread_allocated_bytes=%d retained_heap_bytes=%d meanings=%d%n", elapsed / 1e6, bytes, heap, count);
    RuntimeEvidence.print();
  }
}
