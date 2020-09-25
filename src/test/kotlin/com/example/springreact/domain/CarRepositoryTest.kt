package com.example.springreact.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class CarRepositoryTest(
    @Autowired val testEntityManager: TestEntityManager,
    @Autowired val carRepository: CarRepository
) {
    @Test
    fun `Save car`() {
        val car = Car(
            brand = "Tesla",
            model = "Model X",
            color = "White",
            registerNumber = "ABC-1234",
            year = 2017,
            price = 86000
        )

        val addedCar = testEntityManager.persistAndFlush(car)

        assertThat(addedCar.id).isNotNull()
    }

    @Test
    fun `Delete all cars`() {
        testEntityManager.persistAndFlush(
            Car(
                brand = "Tesla",
                model = "Model X",
                color = "White",
                registerNumber = "ABC-1234",
                year = 2017,
                price = 86000
            )
        )
        testEntityManager.persistAndFlush(
            Car(
                brand = "Mini",
                model = "Cooper",
                color = "Yellow",
                registerNumber = "BWS-3007",
                year = 2015,
                price = 24500
            )
        )

        assertThat(carRepository.findAll()).isNotEmpty
        carRepository.deleteAll()
        assertThat(carRepository.findAll()).isEmpty()
    }
}
