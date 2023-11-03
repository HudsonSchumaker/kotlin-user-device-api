package com.schumaker.api.device.service

import com.schumaker.api.DeviceTestHelper
import com.schumaker.api.device.model.DeviceRepository
import com.schumaker.api.exception.DeviceAlreadyExistsException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DeviceServiceTestIT {

    @Autowired
    private lateinit var deviceService: DeviceService

    @Autowired
    private lateinit var deviceRepository: DeviceRepository

    @AfterEach
    fun cleanUp() {
        deviceRepository.deleteAll()
    }

    @Test
    fun `should create device`() {
        // Arrange
        val device = DeviceTestHelper.createDeviceWithoutId()

        // Act
        val result = deviceService.create(device)

        // Assert
        Assertions.assertNotNull(result.id)
        Assertions.assertEquals(result.uuid, device.uuid)
        Assertions.assertEquals(result.serialNumber, device.serialNumber)
        Assertions.assertEquals(result.phoneNumber, device.phoneNumber)
    }

    @Test
    fun `should not create device, device already exists`() {
        // Arrange
        val device = DeviceTestHelper.createDeviceWithoutId()
        val saved = deviceService.create(device)

        // Act
        val exception = Assertions.assertThrows(DeviceAlreadyExistsException::class.java) {
            val result = deviceService.create(device)
        }

        // Assert
        val exceptionMessage = exception.message
        Assertions.assertTrue(exceptionMessage!!.contains("Device already exists"))
    }
}
