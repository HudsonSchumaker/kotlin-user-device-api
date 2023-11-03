package com.schumaker.api.user.service.validation

fun interface DeviceAssignValidator {
    fun validate(userId: Long, deviceId: Long)
}
