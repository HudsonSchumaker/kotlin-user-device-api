package com.schumaker.api.device.service

import com.schumaker.api.DeviceTestHelper
import com.schumaker.api.device.model.DeviceRepository
import com.schumaker.api.device.service.validation.DeviceValidator
import com.schumaker.api.exception.DeviceAlreadyExistsException
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class DeviceServiceTest {
    @Mock
    private lateinit var deviceService: DeviceService

    @Mock
    private lateinit var deviceRepository: DeviceRepository

    private val deviceValidators: List<DeviceValidator> = createDeviceValidatorMocks(2)

    @BeforeEach
    fun setUp() {
        deviceService = DeviceService(deviceValidators, deviceRepository)
    }

    @Test
    fun `should create device`() {
        // Arrange
        val device = DeviceTestHelper.createDevice()

        // Mock behaviors
        `when`(deviceRepository.save(device)).thenReturn(device)

        // Act
        val createdDevice = deviceService.create(device)

        // Assert
        assertThat(createdDevice).isEqualTo(device)
    }

    @Test
    fun `should not create device, device already exists`() {
        // Arrange
        val existingDevice = DeviceTestHelper.createDevice()

        // Mock behaviors
        deviceValidators.forEach {
            validator -> Mockito.doThrow(DeviceAlreadyExistsException()).`when`(validator).validate(existingDevice)
        }

        // Act and Assert
        assertThrows<DeviceAlreadyExistsException> {
            deviceService.create(existingDevice)
        }
    }

    private fun createDeviceValidatorMocks(count: Int): List<DeviceValidator> {
        val validators = mutableListOf<DeviceValidator>()
        repeat(count) {
            validators.add(Mockito.mock(DeviceValidator::class.java))
        }
        return validators
    }
}
