package org.otnip.moy.registry

import org.otnip.moy.core.MicroserviceInstance

interface Registry {

    fun observe(path: String, listener: MicroserviceInstanceListener)

    fun register(microserviceInstance: MicroserviceInstance, singleton: Boolean): String?
}