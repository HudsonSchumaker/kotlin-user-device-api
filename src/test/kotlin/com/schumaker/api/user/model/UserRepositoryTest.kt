package com.schumaker.api.user.model

import com.schumaker.api.UserTestHelper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import java.time.LocalDate

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `should find user by firstName, lastName and birthday`() {

        val user = UserTestHelper.createUserWithoutId()
        entityManager.persist(user.address)
        entityManager.flush()

        entityManager.persist(user)
        entityManager.flush()

        val result = userRepository.findByFirstNameAndLastNameAndBirthday(user.firstName, user.lastName, user.birthday)

        assertTrue(result.isPresent)
        assertEquals(user, result.get())
    }

    @Test
    fun `should not find user by firstName, lastName and birthday`() {

        val user = UserTestHelper.createUserWithoutId()
        entityManager.persist(user.address)
        entityManager.flush()

        entityManager.persist(user)
        entityManager.flush()

        val result = userRepository.findByFirstNameAndLastNameAndBirthday(user.firstName, user.lastName, LocalDate.now())

        assertFalse(result.isPresent)
    }
}