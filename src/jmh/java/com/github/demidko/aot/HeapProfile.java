package com.github.demidko.aot;

import java.lang.management.ManagementFactory;
import javax.management.ObjectName;

/** HotSpot-only retained class histogram; separate from timing measurements. */
public class HeapProfile {
  public static void main(String[] args) throws Exception {
    WordformMeaning.lookupForMeanings("замок");
    Object histogram = ManagementFactory.getPlatformMBeanServer().invoke(
        new ObjectName("com.sun.management:type=DiagnosticCommand"), "gcClassHistogram",
        new Object[]{new String[0]}, new String[]{"[Ljava.lang.String;"});
    System.out.println(histogram);
    RuntimeEvidence.print();
  }
}
