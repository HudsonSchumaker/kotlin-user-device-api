package com.schumaker.api.user.service.validation

import com.schumaker.api.device.model.DeviceRepository
import com.schumaker.api.exception.DeviceAlreadyAssignedToAnotherUserException
import org.springframework.stereotype.Component

@Component
class DeviceAlreadyAssignedToAnotherUserValidator(private val deviceRepository: DeviceRepository): DeviceAssignValidator {

    override fun validate(userId: Long, deviceId: Long) {
        val device = deviceRepository.findById(deviceId)
        if (device.isPresent) {
            val user = device.get().user
            if ((user?.id != null) && (user.id != userId)) {
                throw DeviceAlreadyAssignedToAnotherUserException()
            }
        }
    }
}
