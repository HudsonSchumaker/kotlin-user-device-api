package com.schumaker.api.user.service

import com.schumaker.api.device.service.DeviceService
import com.schumaker.api.exception.UserAlreadyExistsException
import com.schumaker.api.user.model.AddressRepository
import com.schumaker.api.user.model.entity.User
import com.schumaker.api.user.model.UserRepository
import com.schumaker.api.user.service.validation.DeviceAssignValidator
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class UserService(private val deviceService: DeviceService, private val addressRepository: AddressRepository,
                  private val userRepository: UserRepository, private val validators: List<DeviceAssignValidator>) {

    @Transactional
    fun create(user: User): User {
        if (userRepository.findByFirstNameAndLastNameAndBirthday(user.firstName, user.lastName, user.birthday).isPresent) {
            throw UserAlreadyExistsException()
        }

        addressRepository.save(user.address)
        return userRepository.save(user)
    }

    @Transactional
    fun assignDevice(userId: Long, deviceId: Long): User {
        val user = userRepository.findById(userId).orElseThrow { EntityNotFoundException("User not found") }
        val device = deviceService.getById(deviceId)

        validators.forEach { validator ->
            validator.validate(userId, deviceId)
        }

        device.user = user
        deviceService.update(device)

        return user
    }

    fun list(pagination: Pageable): Page<User> {
        return userRepository.findAll(pagination)
    }
}
