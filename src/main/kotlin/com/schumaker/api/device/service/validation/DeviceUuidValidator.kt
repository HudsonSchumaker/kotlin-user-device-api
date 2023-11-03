package com.schumaker.api.device.service.validation

import com.schumaker.api.device.model.entity.Device
import com.schumaker.api.device.model.DeviceRepository
import com.schumaker.api.exception.DeviceAlreadyExistsException
import org.springframework.stereotype.Component

@Component
class DeviceUuidValidator(private val deviceRepository: DeviceRepository): DeviceValidator {
    override fun validate(device: Device) {
        if (deviceRepository.findByUuid(device.uuid).isPresent) {
            throw DeviceAlreadyExistsException()
        }
    }
}
