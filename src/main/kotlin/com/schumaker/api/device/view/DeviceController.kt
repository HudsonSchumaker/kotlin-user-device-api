package com.schumaker.api.device.view

import com.schumaker.api.device.model.entity.Device
import com.schumaker.api.device.service.DeviceService
import com.schumaker.api.device.view.dto.DeviceDTO
import com.schumaker.api.device.view.dto.DeviceForm
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.modelmapper.ModelMapper
import org.springframework.http.HttpStatus

@RestController
@RequestMapping("/devices")
@Tag(name = "Devices", description = "Endpoints for managing devices")
class DeviceController(private val deviceService: DeviceService, private val modelMapper: ModelMapper) {

    @Operation(summary = "Create a device")
    @ApiResponse(
        responseCode = "201",
        description = "Device created",
    )
    @PostMapping
    fun create(@RequestBody @Valid form: DeviceForm): ResponseEntity<DeviceDTO> {
        var device = modelMapper.map(form, Device::class.java)
        device = deviceService.create(device)

        val deviceDTO = modelMapper.map(device, DeviceDTO::class.java)
        return ResponseEntity.status(HttpStatus.CREATED).body(deviceDTO)
    }
}
