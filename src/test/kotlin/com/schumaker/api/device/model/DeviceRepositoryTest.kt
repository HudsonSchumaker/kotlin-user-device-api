package com.schumaker.api.device.model

import com.schumaker.api.DeviceTestHelper
import com.schumaker.api.UserTestHelper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class DeviceRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var deviceRepository: DeviceRepository

    @Test
    fun `should find device by serial number`() {
        // Arrange
        val device = DeviceTestHelper.createRandomDeviceWithoutId()
        val serialNumber = device.serialNumber
        entityManager.persist(device)

        // Act
        val foundDevice = deviceRepository.findBySerialNumber(serialNumber)

        // Assert
        assertThat(foundDevice).isPresent
        assertThat(foundDevice.get()).isEqualTo(device)
    }

    @Test
    fun `should find device by UUID`() {
        // Arrange
        val device = DeviceTestHelper.createRandomDeviceWithoutId()
        val uuid = device.uuid
        entityManager.persist(device)

        // Act
        val foundDevice = deviceRepository.findByUuid(uuid)

        // Assert
        assertThat(foundDevice).isPresent
        assertThat(foundDevice.get()).isEqualTo(device)
    }

    @Test
    fun `should find device by userId and deviceId`() {
        // Arrange
        val user = UserTestHelper.createUserWithoutId()
        val address = user.address
        entityManager.persist(address)
        entityManager.persist(user)

        val device = DeviceTestHelper.createDeviceWithoutId()
        device.user = user
        entityManager.persist(device)

        val userId = user.id
        val deviceId = device.id

        // Act
        val foundDevice = deviceRepository.findByUserIdAndDeviceId(userId!!, deviceId!!)

        // Assert
        assertThat(foundDevice).isPresent
        assertThat(foundDevice.get()).isEqualTo(device)
    }
}
