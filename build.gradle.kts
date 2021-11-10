repositories {
  mavenCentral()
  maven("https://jitpack.io")
}
plugins {
  `java-library`
  `maven-publish`
}
dependencies {
  api("com.github.demidko:bits:2021.11.14")
  api("com.github.demidko:aot-bytecode:2021.10.29")
  testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
  testImplementation("org.hamcrest:hamcrest:2.2")
}
tasks.test {
  minHeapSize = "1024m"
  maxHeapSize = "2048m"
  useJUnitPlatform()
}
publishing {
  publications {
    create<MavenPublication>("aot") {
      from(components["java"])
    }
  }
}