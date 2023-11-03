package com.schumaker.api.device.view

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.schumaker.api.DeviceTestHelper
import com.schumaker.api.device.model.DeviceRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import java.text.SimpleDateFormat

@SpringBootTest
@AutoConfigureMockMvc
class DeviceControllerTestIT {

    @Autowired
    private lateinit var deviceRepository: DeviceRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    private var objectMapper: ObjectMapper = ObjectMapper()

    @BeforeEach
    fun setUp() {
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.dateFormat = SimpleDateFormat("yyyy-MM-dd")
    }

    @AfterEach
    fun cleanUp() {
        deviceRepository.deleteAll()
    }

    @Test
    fun `should create device`() {
        // Arrange
        val deviceForm = DeviceTestHelper.createDeviceForm()

        // Act
        mockMvc.perform(
            MockMvcRequestBuilders.post("/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deviceForm)))

                // Assert
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.uuid").value(deviceForm.uuid))
                .andExpect(MockMvcResultMatchers.jsonPath("$.model").value(deviceForm.model))
                .andExpect(MockMvcResultMatchers.jsonPath("$.serialNumber").value(deviceForm.serialNumber))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phoneNumber").value(deviceForm.phoneNumber))
                .andDo(print())
    }

    @Test
    fun `should not create device, device already exists`() {
        // Arrange
        val deviceForm = DeviceTestHelper.createDeviceForm()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deviceForm)))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andDo(print())

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

    @Test
    fun `should not create device, blank serial number`() {
        // Arrange
        val deviceForm = DeviceTestHelper.createDeviceFormWithBlankSerialNumber()

        // Act
        mockMvc.perform(
            MockMvcRequestBuilders.post("/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deviceForm)))

                // Assert
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].field").value("serialNumber"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].error").value("Serial number can not be null or blank"))
                .andDo(print())
    }
}
