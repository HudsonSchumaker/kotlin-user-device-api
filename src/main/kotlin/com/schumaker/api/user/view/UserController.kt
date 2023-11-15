package com.schumaker.api.user.view

import com.schumaker.api.device.view.dto.DeviceDTO
import com.schumaker.api.user.model.entity.User
import com.schumaker.api.user.service.UserService
import com.schumaker.api.user.view.dto.AddressDTO
import com.schumaker.api.user.view.dto.UserDTO
import com.schumaker.api.user.view.dto.UserForm
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.modelmapper.ModelMapper
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.stream.Collectors

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Endpoints for managing users")
class UserController(private val userService: UserService, private val modelMapper: ModelMapper) {

    @Operation(summary = "Create a user")
    @ApiResponse(
        responseCode = "201", description = "User created",
    )
    @PostMapping
    fun create(@RequestBody @Valid form: UserForm): ResponseEntity<UserDTO> {
        var user = modelMapper.map(form, User::class.java)
        user = userService.create(user)

        val userDTO = modelMapper.map(user, UserDTO::class.java)
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO)
    }

    @Operation(summary = "Assign a device to a user")
    @ApiResponse(
        responseCode = "202", description = "Device assigned to user",
    )
    @PostMapping("/assign")
    fun assignDevice(@RequestParam userId: Long, @RequestParam deviceId: Long): ResponseEntity<UserDTO> {
        val user = userService.assignDevice(userId, deviceId)

        val userDTO = UserDTO(
            id = user.id!!,
            firstName = user.firstName,
            lastName = user.lastName,
            address =   AddressDTO(
                street = user.address.street,
                city = user.address.city,
                number = user.address.number,
                zipCode = user.address.zipCode,
                country = user.address.country
            ),
            birthday = user.birthday,
            devices = user.devices.stream().map { d-> DeviceDTO(
                id = d.id!!,
                serialNumber = d.serialNumber,
                uuid =  d.uuid,
                phoneNumber = d.phoneNumber,
                model = d.model,
            )}.collect(Collectors.toList())
        )

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userDTO)
    }

    @Operation(summary = "List users")
    @ApiResponse(
        responseCode = "200", description = "List of users",
    )
    @GetMapping
    fun list(@ParameterObject @PageableDefault(page = 0, size = 10, sort = ["lastName"]) pagination: Pageable): Page<UserDTO> {
        val users = userService.list(pagination)
        return users.map { employee -> modelMapper.map(employee, UserDTO::class.java) }
    }
}
