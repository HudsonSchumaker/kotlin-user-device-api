package com.schumaker.api.user.view

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.schumaker.api.DeviceTestHelper
import com.schumaker.api.UserTestHelper
import com.schumaker.api.device.model.DeviceRepository
import com.schumaker.api.user.model.AddressRepository
import com.schumaker.api.user.model.UserRepository
import com.schumaker.api.user.service.UserService
import com.schumaker.api.user.view.dto.UserDTO
import com.schumaker.api.user.view.dto.UserForm
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.text.SimpleDateFormat
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTestIT {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var deviceRepository: DeviceRepository

    @Autowired
    private lateinit var addressRepository: AddressRepository

    @Autowired
    private lateinit var userRepository: UserRepository

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
        userRepository.deleteAll()
        deviceRepository.deleteAll()
        addressRepository.deleteAll()
    }

    @Test
    fun `should create user`() {
        // Arrange
        val userForm = UserTestHelper.createUserForm()

        // Act
        mockMvc.perform(
           post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userForm)))

                // Assert
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.firstName").value(userForm.firstName))
                .andExpect(jsonPath("$.lastName").value(userForm.lastName))
                .andExpect(jsonPath("$.birthday").value(userForm.birthday.toString()))
                .andExpect(jsonPath("$.address.street").value(userForm.address.street))
                .andExpect(jsonPath("$.address.city").value(userForm.address.city))
                .andExpect(jsonPath("$.address.zipCode").value(userForm.address.zipCode))
                .andExpect(jsonPath("$.address.country").value(userForm.address.country))
                .andDo(print())
    }

    @Test
    fun `should not create user since is already created`() {
        // Arrange
        val userForm = UserTestHelper.createUserForm()
        mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userForm)))
                .andExpect(status().isCreated)
                .andDo(print())

        // Act
        mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userForm)))

                // Assert
                .andExpect(status().isConflict)
                .andExpect(jsonPath("$.title").value("User already exists"))
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
        mockMvc.perform(
            post("/users")
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
    fun `should not create user since city is blank`() {
        // Arrange
        val userForm = UserForm(
            firstName = "John", lastName = "Textor", address = UserTestHelper.createAddressFormBlankCity(),
            birthday = LocalDate.of(1980,6,11)
        )

        // Act
        mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userForm)))

                // Assert
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.[0].field").value("address.city"))
                .andExpect(jsonPath("$.[0].error").value("City can not be null or blank"))
                .andDo(print())
    }

    @Test
    fun `should list users`() {
        val users = UserTestHelper.createListOfUsersWithoutId(2)
        userService.create(users[0])
        userService.create(users[1])

        mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray)
            // User 1
            .andExpect(jsonPath("$.content[0].firstName").value(users[0].firstName))
            .andExpect(jsonPath("$.content[0].lastName").value(users[0].lastName))
            .andExpect(jsonPath("$.content[0].birthday").value(users[0].birthday.toString()))
            .andExpect(jsonPath("$.content[0].address.street").value(users[0].address.street))
            .andExpect(jsonPath("$.content[0].address.city").value(users[0].address.city))
            .andExpect(jsonPath("$.content[0].address.zipCode").value(users[0].address.zipCode))
            .andExpect(jsonPath("$.content[0].address.country").value(users[0].address.country))
            // User 2
            .andExpect(jsonPath("$.content[1].firstName").value(users[1].firstName))
            .andExpect(jsonPath("$.content[1].lastName").value(users[1].lastName))
            .andExpect(jsonPath("$.content[1].birthday").value(users[1].birthday.toString()))
            .andExpect(jsonPath("$.content[1].address.street").value(users[1].address.street))
            .andExpect(jsonPath("$.content[1].address.city").value(users[1].address.city))
            .andExpect(jsonPath("$.content[1].address.zipCode").value(users[1].address.zipCode))
            .andExpect(jsonPath("$.content[1].address.country").value(users[1].address.country))
            .andDo(print())
    }

    @Test
    fun `should assign device to user`() {
        // Arrange
        val device = DeviceTestHelper.createDeviceWithoutId()
        deviceRepository.save(device)

        val user = UserTestHelper.createUserWithoutId()
        userService.create(user)

        // Act
        mockMvc.perform(post("/users/assign")
            .param("userId", user.id.toString())
            .param("deviceId", device.id.toString()))

            // Assert
            .andExpect(status().isAccepted)

            // User
            .andExpect(jsonPath("$.firstName").value(user.firstName))
            .andExpect(jsonPath("$.lastName").value(user.lastName))
            .andExpect(jsonPath("$.birthday").value(user.birthday.toString()))

            // User device
            .andExpect(jsonPath("$.devices[0].uuid").value(device.uuid))
            .andExpect(jsonPath("$.devices[0].model").value(device.model))
            .andExpect(jsonPath("$.devices[0].phoneNumber").value(device.phoneNumber))
            .andExpect(jsonPath("$.devices[0].serialNumber").value(device.serialNumber))
            .andDo(print())
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

        // Act
        mockMvc.perform(
            post("/users/assign")
                .param("userId", user.id.toString())
                .param("deviceId", device1.id.toString()))
                .andExpect(status().isAccepted)
                .andDo(print())

        mockMvc.perform(
            post("/users/assign")
                .param("userId", user.id.toString())
                .param("deviceId", device2.id.toString()))
                .andExpect(status().isAccepted)
                .andDo(print())

        val result = mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray)
            .andDo(print())
            .andReturn()

        val responseContent = result.response.contentAsString
        val rootNode = objectMapper.readTree(responseContent)
        val contentNode = rootNode.get("content")

        val userDTOList = objectMapper.readValue<List<UserDTO>>(
            contentNode.toString(),
            objectMapper.typeFactory.constructCollectionType(List::class.java, UserDTO::class.java)
        )

        val assignedDevices = userDTOList[0].devices

        // Assert
        assertTrue(assignedDevices.any { it.model == device1.model })
        assertTrue(assignedDevices.any { it.uuid == device1.uuid })
        assertTrue(assignedDevices.any { it.model == device2.model })
        assertTrue(assignedDevices.any { it.uuid == device2.uuid })
    }

    @Test
    fun `should not assign device to user because it's already assigned to same user`() {
        // Arrange
        val device = DeviceTestHelper.createDeviceWithoutId()
        deviceRepository.save(device)

        val user = UserTestHelper.createUserWithoutId()
        userService.create(user)

        mockMvc.perform(post("/users/assign")
            .param("userId", user.id.toString())
            .param("deviceId", device.id.toString()))
            .andExpect(status().isAccepted)
            .andDo(print())

        // Act
        mockMvc.perform(post("/users/assign")
            .param("userId", user.id.toString())
            .param("deviceId", device.id.toString()))

            // Assert
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.title").value("Device already assigned to me"))
            .andDo(print())
    }

    @Test
    fun `should not assign device to user because it's already assigned to another user`() {
        // Arrange
        val device = DeviceTestHelper.createDeviceWithoutId()
        deviceRepository.save(device)

        val users = UserTestHelper.createListOfUsersWithoutId(2)
        userService.create(users[0])
        userService.create(users[1])

        mockMvc.perform(post("/users/assign")
            .param("userId", users[0].id.toString())
            .param("deviceId", device.id.toString()))
            .andExpect(status().isAccepted)
            .andDo(print())

        // Act
        mockMvc.perform(post("/users/assign")
            .param("userId", users[1].id.toString())
            .param("deviceId", device.id.toString()))

            // Assert
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.title").value("Device already assigned to another User"))
            .andDo(print())
    }

    @Test
    fun `should not assign device to user because user was not found`() {
        // Arrange
        val device = DeviceTestHelper.createDeviceWithoutId()
        deviceRepository.save(device)

        val userId = 1000L

        // Act
        mockMvc.perform(post("/users/assign")
            .param("userId", userId.toString())
            .param("deviceId", device.id.toString()))

            // Assert
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.title").value("User not found"))
            .andDo(print())
    }

    @Test
    fun `should not assign device to user because device was not found`() {
        // Arrange
        val user = UserTestHelper.createUserWithoutId()
        userService.create(user)

        val deviceId = 1000L

        // Act
        mockMvc.perform(post("/users/assign")
            .param("userId", user.id.toString())
            .param("deviceId", deviceId.toString()))

            // Assert
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.title").value("Device not found"))
            .andDo(print())
    }
}
