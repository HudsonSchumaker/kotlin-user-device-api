package com.schumaker.api.device.view.dto

data class DeviceDTO(val id: Long,
                     val serialNumber: String,
                     val uuid: String,
                     val phoneNumber: String,
                     val model: String,
)
