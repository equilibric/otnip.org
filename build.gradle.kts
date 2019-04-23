// plugin : base
apply<BasePlugin>()

// setup buildscript
buildscript {
    repositories {
        mavenLocal()
        jcenter()
    }

    dependencies {
        classpath("io.ratpack:ratpack-gradle:${properties["otnip_version_ratpack"]}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${properties["otnip_version_kotlin"]}")
        // plugins ( cyclical dependency )
        classpath("org.otnip:otnip-kotlin:twittographics")
    }
}

// all repositories
repositories {
    jcenter()
}

// subprojects
subprojects {
    apply(from = rootProject.file("common.gradle"))
}