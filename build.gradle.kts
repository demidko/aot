repositories {
  mavenCentral()
}
plugins {
  `java-library`
  `maven-publish`
}
dependencies {
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
      groupId = "com.github.demidko"
      artifactId = "aot"
      from(components["java"])
    }
  }
}