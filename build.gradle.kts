import java.util.jar.JarFile

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

repositories {
  mavenCentral()
  maven("https://jitpack.io")
}
plugins {
  `java-library`
  `maven-publish`
}
// A misspelled or unrelated baseline must fail instead of silently loading current classes.
val baselineArchive = providers.gradleProperty("baselineJar").orNull?.let { path ->
  file(path).also { archive ->
    require(archive.isFile) { "baselineJar is not a file: $archive" }
    JarFile(archive).use { jar ->
      require(jar.getJarEntry("com/github/demidko/aot/WordformMeaning.class") != null
        && jar.getJarEntry("mrd.gz") != null) {
        "baselineJar must contain WordformMeaning and mrd.gz: $archive"
      }
    }
  }
}
val baselineClasspath = if (baselineArchive == null) files() else files(baselineArchive)

dependencies {
  api("com.github.demidko:bits:2022.08.06")
  api("com.github.demidko:aot-bytecode:2025.02.15")
  testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
  testImplementation("org.hamcrest:hamcrest:2.2")
}
tasks.test {
  minHeapSize = "1024m"
  maxHeapSize = "2048m"
  useJUnitPlatform()
  classpath = baselineClasspath + classpath
}
publishing {
  publications {
    create<MavenPublication>("aot") {
      from(components["java"])
    }
  }
}

// Isolated tools: neither the fixture generator nor benchmarks run during normal tests.
val jmh by sourceSets.creating
configurations[jmh.implementationConfigurationName].extendsFrom(configurations.implementation.get())
dependencies {
  add(jmh.implementationConfigurationName, sourceSets.main.get().output)
  add(jmh.implementationConfigurationName, "org.openjdk.jmh:jmh-core:1.37")
  add(jmh.annotationProcessorConfigurationName, "org.openjdk.jmh:jmh-generator-annprocess:1.37")
}
tasks.register<JavaExec>("captureCharacterization") {
  dependsOn(tasks.testClasses)
  classpath = baselineClasspath + sourceSets.test.get().runtimeClasspath
  mainClass = "com.github.demidko.aot.CharacterizationTest"
  maxHeapSize = "2g"
  args("src/test/resources/characterization.tsv", "src/test/resources/analyses.tsv")
}
tasks.register<JavaExec>("benchmark") {
  dependsOn(tasks.named(jmh.classesTaskName))
  classpath = baselineClasspath + jmh.runtimeClasspath
  mainClass = "org.openjdk.jmh.Main"
  args(providers.gradleProperty("benchmarkArgs").orElse("").get().split(" ").filter { it.isNotEmpty() })
}
tasks.register<JavaExec>("coldStart") {
  dependsOn(tasks.named(jmh.classesTaskName))
  classpath = baselineClasspath + jmh.runtimeClasspath
  mainClass = "com.github.demidko.aot.ColdStart"
  minHeapSize = "1g"
  maxHeapSize = "1g"
}
tasks.register<JavaExec>("dictionaryDigest") {
  dependsOn(tasks.named(jmh.classesTaskName))
  classpath = baselineClasspath + jmh.runtimeClasspath
  mainClass = "com.github.demidko.aot.DictionaryDigest"
  maxHeapSize = "2g"
}
tasks.register<JavaExec>("heapProfile") {
  dependsOn(tasks.named(jmh.classesTaskName))
  classpath = baselineClasspath + jmh.runtimeClasspath
  mainClass = "com.github.demidko.aot.HeapProfile"
  maxHeapSize = "2g"
}
tasks.register<JavaExec>("probeProfile") {
  dependsOn(tasks.named(jmh.classesTaskName))
  classpath = baselineClasspath + jmh.runtimeClasspath
  mainClass = "com.github.demidko.aot.ProbeProfile"
  maxHeapSize = "1g"
}
tasks.register<JavaExec>("captureReviewCorpus") {
  dependsOn(tasks.named(jmh.classesTaskName))
  classpath = baselineClasspath + jmh.runtimeClasspath
  mainClass = "com.github.demidko.aot.ReviewCorpus"
  maxHeapSize = "2g"
  doFirst { require(providers.gradleProperty("baselineJar").isPresent) { "Provide the original baselineJar" } }
  args("src/jmh/resources/review-queries.tsv", "src/jmh/resources/review-corpus.tsv")
}
