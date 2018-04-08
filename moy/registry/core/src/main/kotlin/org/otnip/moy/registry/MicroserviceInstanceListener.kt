package org.otnip.moy.registry

import org.otnip.moy.core.MicroserviceInstance

interface MicroserviceInstanceListener {

    fun event(instances: List<MicroserviceInstance>)
}