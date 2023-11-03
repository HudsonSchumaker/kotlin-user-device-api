package com.schumaker.api.user.service.validation

import com.schumaker.api.device.model.DeviceRepository
import com.schumaker.api.exception.DeviceAlreadyAssignedToMeException
import org.springframework.stereotype.Component

@Component
class DeviceAlreadyAssignedToMeValidator(private val deviceRepository: DeviceRepository): DeviceAssignValidator {
    override fun validate(userId: Long, deviceId: Long) {
       if (deviceRepository.findByUserIdAndDeviceId(userId, deviceId).isPresent) {
           throw DeviceAlreadyAssignedToMeException()
       }
    }
}