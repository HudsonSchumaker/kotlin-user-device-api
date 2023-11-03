package com.schumaker.api.device.model

import com.schumaker.api.device.model.entity.Device
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DeviceRepository : JpaRepository<Device, Long> {

    fun findBySerialNumber(serialNumber: String): Optional<Device>
    fun findByUuid(uuid: String): Optional<Device>

    @Query("SELECT d FROM Device d WHERE d.user.id = :userId AND d.id = :deviceId")
    fun findByUserIdAndDeviceId(userId: Long, deviceId: Long): Optional<Device>
}
