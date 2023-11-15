package com.schumaker.api.user.view.dto

import com.schumaker.api.device.view.dto.DeviceDTO
import java.time.LocalDate

data class UserDTO(val id: Long,
                   val firstName: String,
                   val lastName: String,
                   val address: AddressDTO,
                   val birthday: LocalDate,
                   val devices: List<DeviceDTO> = ArrayList(),
)
