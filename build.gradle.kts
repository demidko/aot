tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
  mavenCentral()
  maven("https://jitpack.io")
}

plugins {
  `java-library`
  `maven-publish`
}

dependencies {
  api("com.github.demidko:bits:2022.08.06")
  api("com.github.demidko:aot-bytecode:2025.02.15")
  api("xml-apis:xml-apis-ext:1.3.04")
  testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
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
