plugins {
    org.otnip.gradle.plugins.Kotlin::class.java
}

dependencies {
    compile(project(":hbase:hbase-core"))

    compile("org.apache.hbase:hbase-client:${properties["otnip_version_hbase"]}")
    compile("org.apache.hadoop:hadoop-client:${properties["otnip_version_hadoop2"]}")
    // compile 'org.apache.hbase:hbase-server:1.2.6')
    // compile("org.apache.hadoop:hadoop-mapreduce:$otnip_version_hadoop")
}