package com.schumaker.api.device.service.validation

import com.schumaker.api.device.model.entity.Device

fun interface DeviceValidator {
    fun validate(device: Device)
}
