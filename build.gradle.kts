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
tasks.compileJava {
  sourceCompatibility = JavaVersion.VERSION_16.toString()
  targetCompatibility = JavaVersion.VERSION_16.toString()
}
tasks.compileTestJava {
  sourceCompatibility = JavaVersion.VERSION_16.toString()
  targetCompatibility = JavaVersion.VERSION_16.toString()
}
publishing {
  publications {
    create<MavenPublication>("library") {
      groupId = "com.github.demidko"
      from(components["java"])
    }
  }
}