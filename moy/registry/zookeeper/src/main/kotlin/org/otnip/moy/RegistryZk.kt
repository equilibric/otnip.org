package org.otnip.moy

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.otnip.moy.core.MicroserviceInstance
import org.otnip.moy.registry.MicroserviceInstanceListener
import org.otnip.moy.registry.Registry
import mu.KotlinLogging
import org.apache.zookeeper.*
import org.apache.zookeeper.data.ACL
import org.apache.zookeeper.data.Id

private val logger = KotlinLogging.logger {}
private const val ZK_CONNECTION = "zk1.solarrow.otnip.com:2181/moy"
private const val ZK_SESSION_TIMEOUT = 1000L*60L

private const val MOY_CONTEXT = "moy.context"

object RegistryZk : Registry {

    private val ZK_ACL = ACL(ZooDefs.Perms.ALL, Id("world", "anyone"))
    private val zk: ZooKeeper

    init {
        zk = ZooKeeper(connectString, ZK_SESSION_TIMEOUT.toInt()) {
            logger.info { "$connectString : $it" }
        }

        // wait to establish session
        while (zk.sessionId == 0L) {
            logger.info("$connectString : establishing session")
            Thread.sleep(250)
        }
    }

    val connectString: String
        get() {
            return "$ZK_CONNECTION/$context"
        }

    val context: String
        get() {
            return System.getProperty(MOY_CONTEXT, System.getProperty("user.name"))
        }


    fun initPath(path: String) {
        logger.info { path }

        if (zk.exists(path, false) == null) {
            // create full path
            val tokens = path.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var currentPath = ""
            for (i in tokens.indices) {
                if (tokens[i].length > 0) {
                    currentPath += "/" + tokens[i]
                    if (zk.exists(currentPath, false) == null) {
                        logger.info { "$context : " + currentPath }
                        zk.create(currentPath, ByteArray(0), listOf(ZK_ACL), CreateMode.PERSISTENT)
                    }
                }
            }
        }
    }

    override fun observe(path: String, listener: MicroserviceInstanceListener) {
        logger.info { path }

        val watcher = object : Watcher {

            override fun process(event: WatchedEvent?) {

                if (event == null) {
                    observe()
                } else {
                    when (event.type) {
                        Watcher.Event.EventType.NodeDeleted -> observe()
                        Watcher.Event.EventType.NodeChildrenChanged -> observe()
                        else -> logger.warn { "$context : event type unhandled : ${event.type}" }
                    }
                }
            }

            private fun observe() {
                val stat = zk.exists(path, false)
                if (stat != null) {
                    when (stat.dataLength) {
                        0 -> {
                            val microserviceInstances = mutableListOf<MicroserviceInstance>()
                            try {
                                for (child in zk.getChildren(path, this)) {
                                    try {
                                        val data = String(zk.getData(path + "/" + child, false, zk.exists(path, false)))
                                        microserviceInstances += jacksonObjectMapper().readValue(data, MicroserviceInstance::class.java)
                                    } catch (e: KeeperException.NoNodeException) {
                                    }
                                }
                            } catch (ex: KeeperException.NoNodeException) {
                                // nothing to do
                            } finally {
                                listener.event(microserviceInstances)
                            }
                        }
                        else -> {
                            val data = String(zk.getData(path, this, stat))
                            val microserviceInstance = jacksonObjectMapper().readValue(data, MicroserviceInstance::class.java)
                            listener.event(listOf(microserviceInstance))
                        }
                    }
                } else {
                    logger.info { "$context : wait on path : $path" }
                    Thread.sleep(2500)
                    observe()
                }
            }
        }

        watcher.process(null)
    }

    override fun register(microserviceInstance: MicroserviceInstance, singleton: Boolean): String {
        val output: String
        val zkPath = "/${microserviceInstance.path.replace(".", "/")}"
        if (singleton) {
            // remove
            if (zk.exists(zkPath, false) != null) {
                zk.delete(zkPath, 0)
            }

            // init
            val zkPathTokens = zkPath.split("/")
            initPath(zkPathTokens.subList(0, zkPathTokens.size - 1).joinToString("/"))

            // create
            output = zk.create(zkPath, microserviceInstance.bytes, listOf(ZK_ACL), CreateMode.EPHEMERAL)
        } else {
            // init
            initPath(zkPath)

            // create
            output = zk.create("$zkPath/", microserviceInstance.bytes, listOf(ZK_ACL), CreateMode.EPHEMERAL_SEQUENTIAL)
        }

        return output
    }
}