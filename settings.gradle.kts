rootProject.name = "otnip.org"

// gradle plugins
include(":gradle-plugins:kotlin")
project(":gradle-plugins:kotlin").projectDir = File(rootDir, "gradle-plugins/kotlin")

include(":gradle-plugins:logging")
project(":gradle-plugins:logging").projectDir = File(rootDir, "gradle-plugins/logging")

include(":gradle-plugins:ratpack")
project(":gradle-plugins:ratpack").projectDir = File(rootDir, "gradle-plugins/ratpack")

include(":gradle-plugins:webpack")
project(":gradle-plugins:webpack").projectDir = File(rootDir, "gradle-plugins/webpack")

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
