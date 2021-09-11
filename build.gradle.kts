repositories {
  mavenCentral()
}
plugins {
  `java-library`
  `maven-publish`
}
dependencies {
  testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
  implementation("org.hamcrest:hamcrest:2.2")
}
tasks.test {
  useJUnitPlatform()
}
tasks.compileJava {
  sourceCompatibility = "16"
  targetCompatibility = "16"
}
tasks.compileTestJava {
  sourceCompatibility = "16"
  targetCompatibility = "16"
}
publishing {
  publications {
    create<MavenPublication>("library") {
      groupId = "com.github.demidko"
      from(components["java"])
    }
  }
}