rootProject.name = "otnip.org"

// gradle plugins
include(":org.otnip:plugins-kotlin");project(":org.otnip:plugins-kotlin").projectDir = File(rootDir, "gradle-plugins/kotlin")
include(":org.otnip:plugins-logging");project(":org.otnip:plugins-logging").projectDir = File(rootDir, "gradle-plugins/logging")
include(":org.otnip:plugins-ratpack");project(":org.otnip:plugins-ratpack").projectDir = File(rootDir, "gradle-plugins/ratpack")
include(":org.otnip:plugins-webpack");project(":org.otnip:plugins-webpack").projectDir = File(rootDir, "gradle-plugins/webpack")

// hbsae toolin
include(":org.otnip:hbase-core");project(":org.otnip:hbase-core").projectDir = File(rootDir, "hbase/core")
include(":org.otnip:hbase-impl");project(":org.otnip:hbase-impl").projectDir = File(rootDir, "hbase/impl")

// moy - microservices on yan
include(":org.otnip:moy-core");project(":org.otnip:moy-core").projectDir = File(rootDir, "moy/core")
include(":org.otnip:moy-registry-core");project(":org.otnip:moy-registry-core").projectDir = File(rootDir, "moy/registry/core")
include(":org.otnip:moy-registry-zookeeper");project(":org.otnip:moy-registry-zookeeper").projectDir = File(rootDir, "moy/registry/zookeeper")

// dev - development & testing
include(":test")
project(":test").projectDir = File(rootDir, "test")
