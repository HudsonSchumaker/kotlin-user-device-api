package com.schumaker.api.user.view.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class AddressForm(

    @field:NotBlank(message = "Street can not be null or blank")
    @Schema(example = "Borussiaßtrasse", required = true)
    val street: String,

    @field:NotBlank(message = "City can not be null or blank")
    @Schema(example = "Berlin", required = true)
    val city: String,

    @field:NotBlank(message = "Number can not be null or blank")
    @Schema(example = "67B", required = true)
    val number: String,

    @field:NotBlank(message = "Zip code can not be null or blank")
    @Schema(example = "123", required = true)
    val zipCode: String,

    @field:NotBlank(message = "Country can not be null or blank")
    @Schema(example = "Germany", required = true)
    val country: String,
)
