package com.schumaker.api.user.model

import com.schumaker.api.user.model.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.Optional

@Repository
interface UserRepository: JpaRepository<User, Long> {
    fun findByFirstNameAndLastNameAndBirthday(firstName: String, lastName: String, birthday: LocalDate): Optional<User>
}
