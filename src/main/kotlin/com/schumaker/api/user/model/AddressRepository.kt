package com.schumaker.api.user.model

import com.schumaker.api.user.model.entity.Address
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AddressRepository: JpaRepository<Address, Long> {}