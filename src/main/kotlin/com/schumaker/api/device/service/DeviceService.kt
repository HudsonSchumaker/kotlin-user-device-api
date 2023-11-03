package com.schumaker.api.device.service

import com.schumaker.api.device.model.entity.Device
import com.schumaker.api.device.model.DeviceRepository
import com.schumaker.api.device.service.validation.DeviceValidator
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class DeviceService(private val validators: List<DeviceValidator>, private val deviceRepository: DeviceRepository) {

    @Transactional
    fun create(device: Device): Device {
        validators.forEach { validator ->
            validator.validate(device)
        }

        return deviceRepository.save(device)
    }

    @Transactional
    fun update(device: Device) {
        deviceRepository.save(device)
    }

    fun getById(id: Long): Device {
        return deviceRepository.findById(id).orElseThrow{ EntityNotFoundException("Device not found") }
    }
}