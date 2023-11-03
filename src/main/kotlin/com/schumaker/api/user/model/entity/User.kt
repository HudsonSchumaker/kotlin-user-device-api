package com.schumaker.api.user.model.entity

import com.schumaker.api.device.model.entity.Device
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val firstName: String,
    val lastName: String,
    val birthday: LocalDate,

    @OneToOne
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    val address: Address,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    val devices: List<Device> = ArrayList()
)
