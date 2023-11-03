package com.schumaker.api.user.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "addresses")
data class Address(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val street: String,
    val city: String,
    val number: String,
    val zipCode: String,
    val country: String,
)
