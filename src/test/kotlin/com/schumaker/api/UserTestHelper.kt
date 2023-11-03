package com.schumaker.api

import com.schumaker.api.device.view.dto.DeviceDTO
import com.schumaker.api.user.model.entity.Address
import com.schumaker.api.user.model.entity.User
import com.schumaker.api.user.view.dto.AddressDTO
import com.schumaker.api.user.view.dto.AddressForm
import com.schumaker.api.user.view.dto.UserDTO
import com.schumaker.api.user.view.dto.UserForm
import java.time.LocalDate

class UserTestHelper {

    companion object {
        fun createAddressForm(): AddressForm {
            return AddressForm(
                street = "Borussiaßtrasse",
                city = "Berlin",
                number = "67B",
                zipCode = "123",
                country = "Germany"
            )
        }

        fun createAddressFormBlankCity(): AddressForm {
            return AddressForm(
                street = "Borussiaßtrasse",
                city = "",
                number = "67B",
                zipCode = "123",
                country = "Germany"
            )
        }

        fun createAddressDTO(): AddressDTO {
            return AddressDTO(
                street = "Borussiaßtrasse",
                city = "Berlin",
                number = "67B",
                zipCode = "123",
                country = "Germany"
            )
        }

        fun createAddress(): Address {
            return Address(
                street = "Borussiaßtrasse",
                city = "Berlin",
                number = "67B",
                zipCode = "123",
                country = "Germany"
            )
        }

        fun createUserForm(): UserForm {
            return UserForm(
                firstName = "John",
                lastName = "Textor",
                address = createAddressForm(),
                birthday = LocalDate.of(1980,1,1)
            )
        }

        fun createUser(id: Long): User {
            return User(
                id = id,
                firstName = "John",
                lastName = "Textor",
                address = Address(
                    id = 1,
                    street = "Borussiaßtrasse",
                    city = "Berlin",
                    number = "67B",
                    zipCode = "123",
                    country = "Germany"
                ),
                birthday = LocalDate.of(1980,1,1)
            )
        }

        fun createUserWithoutId(): User {
            return User(
                firstName = "John",
                lastName = "Textor",
                address = createAddress(),
                birthday = LocalDate.of(1980,1,1)
            )
        }

        fun createUser(): User { return createUser(1) }

        fun createUserDTO(): UserDTO { return createUserDTO(1) }

        fun createUserDTO(id: Long): UserDTO {
            return UserDTO(
                id = id,
                firstName = "John",
                lastName = "Textor",
                address = createAddressDTO(),
                birthday = LocalDate.of(1980,1,1)
            )
        }

        fun createUserWithDevice(userId: Long, deviceId: Long): User {
            return User(
                id = userId,
                firstName = "John",
                lastName = "Textor",
                address = Address(
                    id = 1,
                    street = "Borussiaßtrasse",
                    city = "Berlin",
                    number = "67B",
                    zipCode = "123",
                    country = "Germany"
                ),
                birthday = LocalDate.of(1980,1,1),
                devices = listOf(DeviceTestHelper.createDevice(deviceId))
            )
        }

        fun createUserWithDevice(): User  { return createUserWithDevice(1, 1 ) }

        fun createUserDTOWithDeviceDTO(): UserDTO { return createUserDTOWithDeviceDTO(1, 1) }

        fun createUserDTOWithDeviceDTO(userId: Long, deviceId: Long): UserDTO {
            return UserDTO(
                id = userId,
                firstName = "John",
                lastName = "Textor",
                address = createAddressDTO(),
                birthday = LocalDate.of(1980,1,1),
                devices = listOf(DeviceTestHelper.createDeviceDTO(deviceId))
            )
        }

        fun createListOfUsers(count: Int): List<User> {
            val users = mutableListOf<User>()
            for (i in 1..count) {
                val user = User(
                    id = i.toLong(),
                    firstName = "User_$i",
                    lastName = "Lastname_$i",
                    birthday = LocalDate.of(1990, 1, i),
                    devices = listOf(DeviceTestHelper.createRandomDevice(i.toLong())),
                    address = Address(
                        id = i.toLong(),
                        street = "Alexanderplatz",
                        city = "Berlin",
                        number = "B$i",
                        zipCode = "123$i",
                        country = "Germany"
                    ),
                )
                users.add(user)
            }
            return users
        }

        fun createListOfUsersWithoutId(count: Int): List<User> {
            val users = mutableListOf<User>()
            for (i in 1..count) {
                val user = User(
                    firstName = "User_$i",
                    lastName = "Lastname_$i",
                    birthday = LocalDate.of(1990, 1, i),
                    address = Address(
                        street = "Alexanderplatz",
                        city = "Berlin",
                        number = "B$i",
                        zipCode = "123$i",
                        country = "Germany"
                    ),
                )
                users.add(user)
            }
            return users
        }

        fun mapUsers2UserDTOs(users: List<User>): List<UserDTO> {
            return users.map { user ->
                UserDTO(
                    id = user.id!!,
                    firstName = user.firstName,
                    lastName = user.lastName,
                    address = AddressDTO(
                        street = user.address.street,
                        city = user.address.city,
                        number = user.address.number,
                        zipCode = user.address.zipCode,
                        country = user.address.country
                    ),
                    birthday = user.birthday,
                    devices = listOf(DeviceDTO(
                        id = user.devices[0].id!!,
                        serialNumber = user.devices[0].serialNumber,
                        uuid = user.devices[0].uuid,
                        phoneNumber = user.devices[0].phoneNumber,
                        model = user.devices[0].model,
                    ))
                )
           }
       }
   }
}
