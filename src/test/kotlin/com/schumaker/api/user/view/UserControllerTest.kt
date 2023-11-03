package com.schumaker.api.user.view

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.schumaker.api.UserTestHelper
import com.schumaker.api.exception.DeviceAlreadyAssignedToAnotherUserException
import com.schumaker.api.exception.DeviceAlreadyAssignedToMeException
import com.schumaker.api.user.model.entity.User
import com.schumaker.api.user.service.UserService
import com.schumaker.api.user.view.dto.UserDTO
import com.schumaker.api.user.view.dto.UserForm
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.*
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.text.SimpleDateFormat
import java.time.LocalDate

@WebMvcTest(UserController::class)
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var userService: UserService

    @MockBean
    private lateinit var modelMapper: ModelMapper

    @BeforeEach
    fun setUp() {
        objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.setDateFormat(SimpleDateFormat("yyyy-MM-dd"))
    }

    @Test
    fun `should create user`() {
        // Arrange
        val userForm = UserTestHelper.createUserForm()
        val createdUser = UserTestHelper.createUser()
        val userDTO = UserTestHelper.createUserDTO()

        // Mock behaviors
        `when`(userService.create(createdUser)).thenReturn(createdUser)
        `when`(modelMapper.map(userForm, User::class.java)).thenReturn(createdUser)
        `when`(modelMapper.map(createdUser, UserDTO::class.java)).thenReturn(userDTO)

        // Act
        mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userForm)))

            // Assert
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.firstName").value(createdUser.firstName))
            .andExpect(jsonPath("$.lastName").value(createdUser.lastName))
            .andExpect(jsonPath("$.birthday").value(createdUser.birthday.toString()))
            .andExpect(jsonPath("$.address.street").value(createdUser.address.street))
            .andExpect(jsonPath("$.address.city").value(createdUser.address.city))
            .andExpect(jsonPath("$.address.zipCode").value(createdUser.address.zipCode))
            .andExpect(jsonPath("$.address.country").value(createdUser.address.country))
            .andDo(print())
    }

    @Test
    fun `should not create user since birthday is in the future`() {
        // Arrange
        val userForm = UserForm(
            firstName = "John", lastName = "Textor", address = UserTestHelper.createAddressForm(),
            birthday = LocalDate.of(LocalDate.now().year.inc(),6,11)
        )

        // Act
        mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userForm)))
            // Assert
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.[0].field").value("birthday"))
            .andExpect(jsonPath("$.[0].error").value("Date of birth should be a past date"))
            .andDo(print())
    }

    @Test
    fun `should not create user since first name is blank`() {
        // Arrange
        val userForm = UserForm(
            firstName = "", lastName = "Textor", address = UserTestHelper.createAddressForm(),
            birthday = LocalDate.of(1980,6,11)
        )

        // Act
        mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userForm)))

            // Assert
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.[0].field").value("firstName"))
            .andExpect(jsonPath("$.[0].error").value("First name can not be null or blank"))
            .andDo(print())
    }

    @Test
    fun `should assign device to user`() {
        // Arrange
        val userId = 1L
        val deviceId = 2L
        val assignedUser = UserTestHelper.createUserWithDevice()
        val userDTO = UserTestHelper.createUserDTOWithDeviceDTO()

        // Mock behaviors
        `when`(userService.assignDevice(userId, deviceId)).thenReturn(assignedUser)
        `when`(modelMapper.map(assignedUser, UserDTO::class.java)).thenReturn(userDTO)

        // Act
        mockMvc.perform(post("/users/assign")
            .param("userId", userId.toString())
            .param("deviceId", deviceId.toString()))

            // Assert
            .andExpect(status().isAccepted)
            .andExpect(jsonPath("$.firstName").value(assignedUser.firstName))
            .andExpect(jsonPath("$.lastName").value(assignedUser.lastName))
            .andExpect(jsonPath("$.birthday").value(assignedUser.birthday.toString()))
            .andExpect(jsonPath("$.devices[0].serialNumber").value(assignedUser.devices[0].serialNumber))
            .andExpect(jsonPath("$.devices[0].model").value(assignedUser.devices[0].model))
            .andExpect(jsonPath("$.devices[0].phoneNumber").value(assignedUser.devices[0].phoneNumber))
            .andDo(print())
    }

    @Test
    fun `should not assign device to user because it's already assigned to the same user`() {
        // Arrange
        val user = UserTestHelper.createUserWithDevice()
        val userId = user.id
        val deviceId =  user.devices[0].id

        // Mock behavior
        `when`(userService.assignDevice(userId!!, deviceId!!)).thenThrow(DeviceAlreadyAssignedToMeException())

        // Act
        mockMvc.perform(post("/users/assign")
            .param("userId", userId.toString())
            .param("deviceId", deviceId.toString()))

            // Assert
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.title").value("Device already assigned to me"))
            .andDo(print())
    }

    @Test
    fun `should not assign device to user because it's already assigned to another user`() {
        // Arrange
        val user = UserTestHelper.createUserWithDevice()
        val userId = user.id
        val deviceId =  user.devices[0].id

        // Mock behavior
        `when`(userService.assignDevice(userId!!, deviceId!!)).thenThrow(DeviceAlreadyAssignedToAnotherUserException())

        // Act
        mockMvc.perform(post("/users/assign")
            .param("userId", userId.toString())
            .param("deviceId", deviceId.toString()))

            // Assert
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.title").value("Device already assigned to another User"))
            .andDo(print())
    }

    @Test
    fun `should not assign device to user because user was not found`() {
        // Arrange
        val userId = 1L
        val deviceId = 2L

        // Mock behavior
        `when`(userService.assignDevice(userId, deviceId)).thenThrow(EntityNotFoundException("User not found"))

        // Act
        mockMvc.perform(post("/users/assign")
            .param("userId", userId.toString())
            .param("deviceId", deviceId.toString()))

            // Assert
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.title").value("User not found"))
            .andDo(print())
    }

    @Test
    fun `should not assign device to user because device was not found`() {
        // Arrange
        val userId = 1L
        val deviceId = 2L

        // Mock behavior
        `when`(userService.assignDevice(userId, deviceId)).thenThrow(EntityNotFoundException("Device not found"))

        // Act
        mockMvc.perform(post("/users/assign")
            .param("userId", userId.toString())
            .param("deviceId", deviceId.toString()))

            // Assert
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.title").value("Device not found"))
            .andDo(print())
    }

    @Test
    fun `should list users`() {
        // Arrange
        val userList = UserTestHelper.createListOfUsers(2)
        val userDTOs = UserTestHelper.mapUsers2UserDTOs(userList)
        val users: Page<User> = PageImpl(userList)
        val pageable: Pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("lastName")))

        // Mock behaviors
        `when`(userService.list(pageable)).thenReturn(users)
        `when`(modelMapper.map(userList[0], UserDTO::class.java)).thenReturn(userDTOs[0])
        `when`(modelMapper.map(userList[1], UserDTO::class.java)).thenReturn(userDTOs[1])

        // Act
        mockMvc.perform(get("/users"))

            // Assert
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            // User 1
            .andExpect(jsonPath("$.content[0].id").value(userList[0].id))
            .andExpect(jsonPath("$.content[0].firstName").value(userList[0].firstName))
            .andExpect(jsonPath("$.content[0].lastName").value(userList[0].lastName))
            .andExpect(jsonPath("$.content[0].birthday").value(userList[0].birthday.toString()))

            // User 1 device
            .andExpect(jsonPath("$.content[0].devices[0].serialNumber").value(userList[0].devices[0].serialNumber))
            .andExpect(jsonPath("$.content[0].devices[0].model").value(userList[0].devices[0].model))

            // User 2
            .andExpect(jsonPath("$.content[1].firstName").value(userList[1].firstName))
            .andExpect(jsonPath("$.content[1].lastName").value(userList[1].lastName))
            .andExpect(jsonPath("$.content[1].birthday").value(userList[1].birthday.toString()))

            // User 2 device
            .andExpect(jsonPath("$.content[1].devices[0].serialNumber").value(userList[1].devices[0].serialNumber))
            .andExpect(jsonPath("$.content[1].devices[0].model").value(userList[1].devices[0].model))
            .andDo(print())

            // Verify that userService.list() was called
            verify(userService).list(pageable)
    }
}
