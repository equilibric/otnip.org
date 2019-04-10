plugins {
    id("otnip-kotlin")
}

dependencies {
    compile(project(":moy:moy-registry-core"))
    compile("org.apache.zookeeper:zookeeper:${properties["otnip_version_zookeeper"]}")
    compile("org.apache.avro:avro-ipc:${properties["otnip_version_avro"]}")
}