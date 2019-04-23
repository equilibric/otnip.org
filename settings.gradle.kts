rootProject.name = "otnip.org"

// gradle plugins
include(":gradle-plugins:otnip-kotlin");project(":gradle-plugins:otnip-kotlin").projectDir = File(rootDir, "gradle-plugins/kotlin")
include(":gradle-plugins:otnip-logging");project(":gradle-plugins:otnip-logging").projectDir = File(rootDir, "gradle-plugins/logging")
include(":gradle-plugins:otnip-ratpack");project(":gradle-plugins:otnip-ratpack").projectDir = File(rootDir, "gradle-plugins/ratpack")
include(":gradle-plugins:otnip-webpack");project(":gradle-plugins:otnip-webpack").projectDir = File(rootDir, "gradle-plugins/webpack")

/**
 * hbase tools
 */
include(":hbase:hbase-core");project(":hbase:hbase-core").projectDir = File(rootDir, "hbase/core")
include(":hbase:hbase-impl");project(":hbase:hbase-impl").projectDir = File(rootDir, "hbase/impl")

/**
 * moy - microservices on yarn
 */
include(":moy:moy-core")
project(":moy:moy-core").projectDir = File(rootDir, "moy/core")

include(":moy:moy-registry-core")
project(":moy:moy-registry-core").projectDir = File(rootDir, "moy/registry/core")

include(":moy:moy-registry-zookeeper")
project(":moy:moy-registry-zookeeper").projectDir = File(rootDir, "moy/registry/zookeeper")

/**
 * dev - development & testing
 */
include(":test")
project(":test").projectDir = File(rootDir, "test")
