import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.2"
    id("io.spring.dependency-management") version "1.0.12.RELEASE"

    val kotlinVersion = "1.6.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    kotlin("kapt") version kotlinVersion
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // 1) querydsl-jpa 의존성 추가
    implementation("com.querydsl:querydsl-jpa")

    // 2) @Entity가 선언되있는 클래스를 (default) build 폴더 하위에 QClass 생성
    kapt(group = "com.querydsl", name = "querydsl-apt", classifier = "jpa")

    // 3) build 폴더 하위에 생성된 QClass를 프로젝트 내부에 import하도록 도와주는 설정
    sourceSets.main {
        setBuildDir(buildDir)
//        setBuildDir("$buildDir/generated/source/kapt/main")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
