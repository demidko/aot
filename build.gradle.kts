repositories {
  mavenCentral()
}
plugins {
  `java-library`
  `maven-publish`
}
dependencies {
  testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
  testImplementation("org.hamcrest:hamcrest:2.2")
}
tasks.test {
  useJUnitPlatform()
}
publishing {
  publications {
    create<MavenPublication>("library") {
      groupId = "com.github.demidko"
      from(components["java"])
    }
  }
}