package com.schumaker.api.device.view.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class DeviceForm(
    @field:NotBlank(message = "Serial number can not be null or blank")
    @Schema(example = "ABC-123", required = true)
    val serialNumber: String,

    @field:NotBlank(message = "Uuid can not be null or blank")
    @Schema(example = "e58ed763-928c-4155-bee9-fdbaaadc15f3", required = true)
    val uuid: String,

    @field:NotBlank(message = "Phone number can not be null or blank")
    @Schema(example = "+49 172 234567", required = true)
    val phoneNumber: String,

    @field:NotBlank(message = "Model can not be null or blank")
    @Schema(example = "Model S", required = true)
    val model: String,
)
