package com.schumaker.api.user.view.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Past
import java.time.LocalDate

data class UserForm(

    @field:NotBlank(message = "First name can not be null or blank")
    @Schema(example = "John", required = true)
    val firstName: String,

    @field:NotBlank(message = "Last name can not be null or blank")
    @Schema(example = "Doe", required = true)
    val lastName: String,

    @field:Valid
    val address: AddressForm,

    @Schema(example = "1990-08-01", required = true)
    @field:Past(message = "Date of birth should be a past date")
    @field:NotNull(message = "Date of birth can not be null")
    val birthday: LocalDate,
)
