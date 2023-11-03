package com.schumaker.api

import com.schumaker.api.device.model.entity.Device
import com.schumaker.api.device.view.dto.DeviceDTO
import com.schumaker.api.device.view.dto.DeviceForm
import java.util.*
import kotlin.random.Random

class DeviceTestHelper {

    companion object {

        fun createDeviceForm(): DeviceForm {
            return DeviceForm(
                serialNumber = "ABC-123",
                uuid = "e58ed763-928c-4155-bee9-fdbaaadc15f3",
                phoneNumber = "+49 172 234567",
                model = "Model S",
            )
        }

        fun createDeviceFormWithBlankSerialNumber(): DeviceForm {
            return DeviceForm(
                serialNumber = "",
                uuid = "e58ed763-928c-4155-bee9-fdbaaadc15f3",
                phoneNumber = "+49 172 234567",
                model = "Model S",
            )
        }

        fun createDevice(): Device { return createDevice(1) }

        fun createDevice(id: Long): Device {
            return Device(
                id = id,
                serialNumber = "ABC-123",
                uuid = "e58ed763-928c-4155-bee9-fdbaaadc15f3",
                phoneNumber = "+49 172 234567",
                model = "Model S",
                user = null
            )
        }

        fun createDeviceWithoutId(): Device {
            return Device(
                serialNumber = "ABC-123",
                uuid = "e58ed763-928c-4155-bee9-fdbaaadc15f3",
                phoneNumber = "+49 172 234567",
                model = "Model S",
                user = null
            )
        }

        fun createRandomDeviceWithoutId(): Device {
            return Device(
                serialNumber = generateRandomSerialNumber(),
                uuid = UUID.randomUUID().toString(),
                phoneNumber = generateRandomPhoneNumber(),
                model = generateRandomModel(),
                user = null
            )
        }

        fun createDeviceDTO(): DeviceDTO { return createDeviceDTO(1) }

        fun createDeviceDTO(id: Long): DeviceDTO {
            return DeviceDTO(
                id = id,
                serialNumber = "ABC-123",
                uuid = "e58ed763-928c-4155-bee9-fdbaaadc15f3",
                phoneNumber = "+49 172 234567",
                model = "Model S",
            )
        }

        fun createRandomDevice(): Device { return createRandomDevice(1) }

        fun createRandomDevice(id: Long): Device {
            return Device(
                id = id,
                serialNumber = generateRandomSerialNumber(),
                uuid = UUID.randomUUID().toString(),
                phoneNumber = generateRandomPhoneNumber(),
                model = generateRandomModel(),
                user = null
            )
        }

        fun createRandomDeviceDTO(): DeviceDTO { return createRandomDeviceDTO(1) }

        fun createRandomDeviceDTO(id: Long): DeviceDTO {
            return DeviceDTO(
                id = id,
                serialNumber = generateRandomSerialNumber(),
                uuid = UUID.randomUUID().toString(),
                phoneNumber = generateRandomPhoneNumber(),
                model = generateRandomModel(),
            )
        }

        private fun generateRandomSerialNumber(): String {
            return "ABC-${Random.nextInt(1000, 9999)}"
        }

        private fun generateRandomPhoneNumber(): String {
            return "+49 ${Random.nextInt(100, 999)} ${Random.nextInt(1000000, 9999999)}"
        }

        private fun generateRandomModel(): String {
            val modelNames = listOf("Model S", "Model 3", "Model X", "Model Y")
            return modelNames[Random.nextInt(modelNames.size)]
        }
    }
}