package com.github.demidko.aot;

import java.lang.management.ManagementFactory;
import java.lang.management.GarbageCollectorMXBean;
import java.util.StringJoiner;

/** Provenance for diagnostic runs; deliberately printed after cold-start measurements. */
final class RuntimeEvidence {
  static void print() {
    StringJoiner collectors = new StringJoiner(",");
    for (GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans())
      collectors.add(bean.getName());
    System.err.printf("implementation=%s java=%s vm=%s collectors=%s initial_heap_bytes=%d max_heap_bytes=%d%n",
        WordformMeaning.class.getProtectionDomain().getCodeSource().getLocation(),
        System.getProperty("java.version"), System.getProperty("java.vm.name"), collectors,
        ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getInit(), Runtime.getRuntime().maxMemory());
  }
}
