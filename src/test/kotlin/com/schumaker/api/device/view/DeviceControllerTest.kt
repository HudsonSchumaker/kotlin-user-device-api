package com.schumaker.api.device.view

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.schumaker.api.DeviceTestHelper
import com.schumaker.api.device.model.entity.Device
import com.schumaker.api.device.service.DeviceService
import com.schumaker.api.device.view.dto.DeviceDTO
import com.schumaker.api.exception.DeviceAlreadyExistsException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import java.text.SimpleDateFormat

@WebMvcTest(DeviceController::class)
class DeviceControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var deviceService: DeviceService

    @MockBean
    private lateinit var modelMapper: ModelMapper

    @BeforeEach
    fun setUp() {
        objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.setDateFormat(SimpleDateFormat("yyyy-MM-dd"))
    }

    @Test
    fun `should create device`() {
        // Arrange
        val deviceForm = DeviceTestHelper.createDeviceForm()
        val createdDevice = DeviceTestHelper.createDevice()
        val deviceDTO = DeviceTestHelper.createDeviceDTO()

        // Mock behaviors
        Mockito.`when`(deviceService.create(createdDevice)).thenReturn(createdDevice)
        Mockito.`when`(modelMapper.map(deviceForm, Device::class.java)).thenReturn(createdDevice)
        Mockito.`when`(modelMapper.map(createdDevice, DeviceDTO::class.java)).thenReturn(deviceDTO)

        // Act
        mockMvc.perform(
            MockMvcRequestBuilders.post("/devices")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(deviceForm)))

            // Assert
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.uuid").value(createdDevice.uuid))
            .andExpect(MockMvcResultMatchers.jsonPath("$.model").value(createdDevice.model))
            .andExpect(MockMvcResultMatchers.jsonPath("$.serialNumber").value(createdDevice.serialNumber))
            .andExpect(MockMvcResultMatchers.jsonPath("$.phoneNumber").value(createdDevice.phoneNumber))
            .andDo(print())
    }

    @Test
    fun `should not create device, device already exists`() {
        // Arrange
        val deviceForm = DeviceTestHelper.createDeviceForm()
        val createdDevice = DeviceTestHelper.createDevice()

        // Mock behaviors
        Mockito.`when`(deviceService.create(createdDevice)).thenReturn(createdDevice)
        Mockito.`when`(modelMapper.map(deviceForm, Device::class.java)).thenReturn(createdDevice)
        Mockito.`when`(deviceService.create(createdDevice)).thenThrow(DeviceAlreadyExistsException())

        // Act
        mockMvc.perform(
            MockMvcRequestBuilders.post("/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deviceForm)))

            // Assert
            .andExpect(MockMvcResultMatchers.status().isConflict)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Device already exists"))
            .andDo(print())
    }
}
