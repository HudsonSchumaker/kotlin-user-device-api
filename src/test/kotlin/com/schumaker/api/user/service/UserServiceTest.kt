package com.schumaker.api.user.service

import com.schumaker.api.DeviceTestHelper
import com.schumaker.api.UserTestHelper
import com.schumaker.api.device.service.DeviceService
import com.schumaker.api.exception.DeviceAlreadyAssignedToAnotherUserException
import com.schumaker.api.exception.DeviceAlreadyAssignedToMeException
import com.schumaker.api.exception.UserAlreadyExistsException
import com.schumaker.api.user.model.AddressRepository
import com.schumaker.api.user.model.UserRepository
import com.schumaker.api.user.service.validation.DeviceAssignValidator
import jakarta.persistence.EntityNotFoundException
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.util.*

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock
    private lateinit var userService: UserService

    @Mock
    private lateinit var deviceService: DeviceService

    @Mock
    private lateinit var addressRepository: AddressRepository

    @Mock
    private lateinit var userRepository: UserRepository

    private val deviceAssignValidators: List<DeviceAssignValidator> = createDeviceAssignValidatorMocks(2)

    @BeforeEach
    fun setUp() {
        userService = UserService(deviceService, addressRepository, userRepository, deviceAssignValidators)
    }

    @Test
    fun `should create user`() {
        // Arrange
        val user = UserTestHelper.createUserWithoutId()

        // Mock behaviors
        `when`(userRepository.findByFirstNameAndLastNameAndBirthday(user.firstName, user.lastName, user.birthday)).thenReturn(Optional.empty())
        `when`(addressRepository.save(user.address)).thenReturn(user.address)
        `when`(userRepository.save(user)).thenReturn(user)

        // Act
        val createdUser = userService.create(user)

        // Assert
        assertThat(createdUser).isEqualTo(user)
    }

    @Test
    fun `should not create user since is already created`() {
        // Arrange
        val user = UserTestHelper.createUserWithoutId()

        // Mock behavior
        `when`(userRepository.findByFirstNameAndLastNameAndBirthday(user.firstName, user.lastName, user.birthday)).thenReturn(Optional.of(user))

        // Act and Assert
        assertThrows<UserAlreadyExistsException> {
            userService.create(user)
        }
    }

    @Test
    fun `should list users`() {
        // Arrange
        val pageable = Pageable.unpaged()
        val userList = UserTestHelper.createListOfUsers(2)
        val page = PageImpl(userList, pageable, userList.size.toLong())

        // Mock behavior
        `when`(userRepository.findAll(pageable)).thenReturn(page)

        // Act
        val result = userService.list(pageable)

        // Assert
        assertThat(result.content).isEqualTo(userList)
        assertThat(result.pageable).isEqualTo(pageable)
        assertThat(result.totalElements).isEqualTo(userList.size.toLong())
    }

    @Test
    fun `should assign device to user`() {
        // Arrange
        val user = UserTestHelper.createUser()
        val device = DeviceTestHelper.createRandomDevice()

        // Mock behaviors
        `when`(userRepository.findById(user.id!!)).thenReturn(Optional.of(user))
        `when`(deviceService.getById(device.id!!)).thenReturn(device)

        deviceAssignValidators.forEach {
            validator -> doNothing().`when`(validator).validate(user.id!!, device.id!!)
        }

        // Act
        val assignedUser = userService.assignDevice(user.id!!, device.id!!)

        // Assert
        assertThat(assignedUser).isEqualTo(user)
        assertThat(device.user).isEqualTo(user)
    }

    @Test
    fun `should assign two devices to user`() {
        // Arrange
        val user = UserTestHelper.createUser()
        val device1 = DeviceTestHelper.createRandomDevice()
        val device2 = DeviceTestHelper.createRandomDevice()
        device1.user = user
        device2.user = user

        `when`(userRepository.findById(user.id!!)).thenReturn(Optional.of(user))
        `when`(deviceService.getById(device1.id!!)).thenReturn(device1)
        `when`(deviceService.getById(device2.id!!)).thenReturn(device2)

        deviceAssignValidators.forEach {
            validator -> doNothing().`when`(validator).validate(user.id!!, device1.id!!)
        }

        // Act
        val assignedUser1 = userService.assignDevice(user.id!!, device1.id!!)
        val assignedUser2 = userService.assignDevice(user.id!!, device2.id!!)

        // Assert
        assertThat(assignedUser1).isEqualTo(user)
        assertThat(assignedUser2).isEqualTo(user)
        assertThat(device1.user).isEqualTo(user)
        assertThat(device2.user).isEqualTo(user)
    }

    @Test
    fun `should not assign device to user because it's already assigned to same user`() {
        // Arrange
        val user = UserTestHelper.createUser()
        val device = DeviceTestHelper.createRandomDevice()

        // Mock behaviors
        deviceAssignValidators.forEach {
            validator -> doThrow(DeviceAlreadyAssignedToMeException()).`when`(validator).validate(user.id!!, device.id!!)
        }

        `when`(userRepository.findById(user.id!!)).thenReturn(Optional.of(user))
        `when`(deviceService.getById(device.id!!)).thenReturn(device)

        // Act and Assert
        assertThrows<DeviceAlreadyAssignedToMeException> {
            userService.assignDevice(user.id!!, device.id!!)
        }
    }

    @Test
    fun `should not assign device to user because it's already assigned to another user`() {
        // Arrange
        val user = UserTestHelper.createUser()
        val device = DeviceTestHelper.createRandomDevice()

        // Mock behaviors
        deviceAssignValidators.forEach {
            validator -> doThrow(DeviceAlreadyAssignedToAnotherUserException())
                .`when`(validator).validate(user.id!!, device.id!!)
        }

        `when`(userRepository.findById(user.id!!)).thenReturn(Optional.of(user))
        `when`(deviceService.getById(device.id!!)).thenReturn(device)

        // Act and Assert
        assertThrows<DeviceAlreadyAssignedToAnotherUserException> {
            userService.assignDevice(user.id!!, device.id!!)
        }
    }

    @Test
    fun `should not assign device to user because user was not found`() {
        // Arrange
        val userId = 1L
        val device = DeviceTestHelper.createRandomDevice()

        // Mock behavior
        `when`(userRepository.findById(userId)).thenReturn(Optional.empty())

        // Act and Assert
        assertThrows<EntityNotFoundException> {
            userService.assignDevice(userId, device.id!!)
        }
    }

    @Test
    fun `should not assign device to user because device was not found`() {
        // Arrange
        val userId = 1L
        val deviceId = 1L

        // Mock behavior
        `when`(userRepository.findById(userId)).thenReturn(Optional.of(UserTestHelper.createUser()))
        `when`(deviceService.getById(deviceId)).thenThrow(EntityNotFoundException::class.java)

        // Act and Assert
        assertThrows<EntityNotFoundException> {
            userService.assignDevice(userId, deviceId)
        }
    }

    private fun createDeviceAssignValidatorMocks(count: Int): List<DeviceAssignValidator> {
        val validators = mutableListOf<DeviceAssignValidator>()
        repeat(count) {
            validators.add(mock(DeviceAssignValidator::class.java))
        }
        return validators
    }
}
