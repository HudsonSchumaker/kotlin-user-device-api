package com.schumaker.api.user.service

import com.schumaker.api.DeviceTestHelper
import com.schumaker.api.UserTestHelper
import com.schumaker.api.device.model.DeviceRepository
import com.schumaker.api.exception.DeviceAlreadyAssignedToAnotherUserException
import com.schumaker.api.exception.DeviceAlreadyAssignedToMeException
import com.schumaker.api.exception.UserAlreadyExistsException
import com.schumaker.api.user.model.AddressRepository
import com.schumaker.api.user.model.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Pageable

@SpringBootTest
class UserServiceTestIT {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var deviceRepository: DeviceRepository

    @Autowired
    private lateinit var addressRepository: AddressRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @AfterEach
    fun cleanUp() {
        userRepository.deleteAll()
        deviceRepository.deleteAll()
        addressRepository.deleteAll()
    }

    @Test
    fun `should create user`() {
        // Arrange
        val user = UserTestHelper.createUserWithoutId()

        // Act
        val result = userService.create(user)

        // Assert
        Assertions.assertNotNull(result.id)
        Assertions.assertEquals(result.firstName, user.firstName)
        Assertions.assertEquals(result.lastName, user.lastName)
        Assertions.assertEquals(result.birthday, user.birthday)
    }

    @Test
    fun `should not create user since is already created`() {
        // Arrange
        val user = UserTestHelper.createUserWithoutId()
        val saved = userService.create(user)

        // Act
        val exception = Assertions.assertThrows(UserAlreadyExistsException::class.java) {
            val result = userService.create(user)
        }

        // Assert
        val exceptionMessage = exception.message
        Assertions.assertTrue(exceptionMessage!!.contains("User already exists"))
    }

    @Test
    fun `should list users`() {
        // Arrange
        val users = UserTestHelper.createListOfUsersWithoutId(2)
        userService.create(users[0])
        userService.create(users[1])
        val pagination: Pageable = Pageable.ofSize(10)

        // Act
        val result = userService.list(pagination)

        // Assert
        Assertions.assertEquals(result.totalElements, users.size.toLong())
    }

    @Test
    fun `should assign device to user`() {
        // Arrange
        val device = DeviceTestHelper.createDeviceWithoutId()
        deviceRepository.save(device)

        val user = UserTestHelper.createUserWithoutId()
        userService.create(user)

        // Act
        val saved = userService.assignDevice(user.id!!, device.id!!)
        val result = userRepository.findById(saved.id!!).get()


        // Assert
        // User
        Assertions.assertNotNull(result.id)
        Assertions.assertEquals(result.firstName, user.firstName)
        Assertions.assertEquals(result.lastName, user.lastName)
        Assertions.assertEquals(result.birthday, user.birthday)

        // Address
        Assertions.assertEquals(result.address.street, user.address.street)
        Assertions.assertEquals(result.address.city, user.address.city)
        Assertions.assertEquals(result.address.zipCode, user.address.zipCode)
        Assertions.assertEquals(result.address.country, user.address.country)

        // Device
        Assertions.assertEquals(result.devices[0].model, device.model)
        Assertions.assertEquals(result.devices[0].uuid, device.uuid)
        Assertions.assertEquals(result.devices[0].phoneNumber, device.phoneNumber)
        Assertions.assertEquals(result.devices[0].serialNumber, device.serialNumber)
    }

    @Test
    fun `should assign two devices to user`() {
        // Arrange
        val device1 = DeviceTestHelper.createRandomDeviceWithoutId()
        deviceRepository.save(device1)

        val device2 = DeviceTestHelper.createRandomDeviceWithoutId()
        deviceRepository.save(device2)

        val user = UserTestHelper.createUserWithoutId()
        userService.create(user)

        userService.assignDevice(user.id!!, device1.id!!)
        userService.assignDevice(user.id!!, device2.id!!)

        val pagination: Pageable = Pageable.ofSize(10)

        // Act
        val result = userService.list(pagination)
        val assignedDevices = result.content[0].devices

        // Assert
        Assertions.assertTrue(assignedDevices.any { it.model == device1.model })
        Assertions.assertTrue(assignedDevices.any { it.uuid == device1.uuid })
        Assertions.assertTrue(assignedDevices.any { it.model == device2.model })
        Assertions.assertTrue(assignedDevices.any { it.uuid == device2.uuid })
    }

    @Test
    fun `should not assign device to user because is already assigned to same user`() {
        // Arrange
        val device = DeviceTestHelper.createDeviceWithoutId()
        deviceRepository.save(device)

        val user = UserTestHelper.createUserWithoutId()
        userService.create(user)
        userService.assignDevice(user.id!!, device.id!!)

        // Act
        val exception = Assertions.assertThrows(DeviceAlreadyAssignedToMeException::class.java) {
            userService.assignDevice(user.id!!, device.id!!)
        }

        // Assert
        val exceptionMessage = exception.message
        Assertions.assertTrue(exceptionMessage!!.contains("Device already assigned to me"))

    }

    @Test
    fun `should not assign device to user because is already assigned to another user`() {
        // Arrange
        val device = DeviceTestHelper.createDeviceWithoutId()
        deviceRepository.save(device)

        val otherUser = UserTestHelper.createListOfUsersWithoutId(1)

        userService.create(otherUser[0])
        userService.assignDevice(otherUser[0].id!!, device.id!!)

        val user = UserTestHelper.createUserWithoutId()
        userService.create(user)

        // Act
        val exception = Assertions.assertThrows(DeviceAlreadyAssignedToAnotherUserException::class.java) {
            userService.assignDevice(user.id!!, device.id!!)
        }

        // Assert
        val exceptionMessage = exception.message
        Assertions.assertTrue(exceptionMessage!!.contains("Device already assigned to another User"))
    }

    @Test
    fun `should not assign device to user because user was not found`() {
        // Arrange
        val device = DeviceTestHelper.createDeviceWithoutId()
        deviceRepository.save(device)

        val userId = 1000L

        // Act
        val exception = Assertions.assertThrows(EntityNotFoundException::class.java) {
            userService.assignDevice(userId, device.id!!)
        }

        // Assert
        val exceptionMessage = exception.message
        Assertions.assertTrue(exceptionMessage!!.contains("User not found"))
    }

    @Test
    fun `should not assign device to user because device was not found`() {
        // Arrange
        val user = UserTestHelper.createUserWithoutId()
        userService.create(user)

        val deviceId = 1000L

        // Act
        val exception = Assertions.assertThrows(EntityNotFoundException::class.java) {
            userService.assignDevice(user.id!!, deviceId)
        }

        // Assert
        val exceptionMessage = exception.message
        Assertions.assertTrue(exceptionMessage!!.contains("Device not found"))
    }
}