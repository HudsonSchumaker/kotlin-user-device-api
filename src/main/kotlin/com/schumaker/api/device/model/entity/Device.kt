package com.schumaker.api.device.model.entity

import com.schumaker.api.user.model.entity.User
import jakarta.persistence.*

@Entity
@Table(name = "devices")
data class Device(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val serialNumber: String,
    val uuid: String,
    val phoneNumber: String,
    val model: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User?,
)
