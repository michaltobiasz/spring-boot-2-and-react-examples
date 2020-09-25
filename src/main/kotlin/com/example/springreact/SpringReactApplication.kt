package com.example.springreact

import com.example.springreact.domain.Car
import com.example.springreact.domain.CarRepository
import com.example.springreact.domain.Owner
import com.example.springreact.domain.OwnerRepository
import com.example.springreact.security.User
import com.example.springreact.security.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class SpringReactApplication {
    @Bean
    fun runner(
        carRepository: CarRepository,
        ownerRepository: OwnerRepository,
        userRepository: UserRepository
    ) = CommandLineRunner {
        val owner1 = ownerRepository.save(Owner(firstName = "John", lastName = "Johnson"))
        val owner2 = ownerRepository.save(Owner(firstName = "Mary", lastName = "Robinson"))
        carRepository.save(
            Car(
                brand = "Ford",
                model = "Mustang",
                color = "Red",
                registerNumber = "ADF-1121",
                year = 2017,
                price = 59000,
                owner = owner1
            )
        )
        carRepository.save(
            Car(
                brand = "Nissan",
                model = "Leaf",
                color = "White",
                registerNumber = "SSJ-3002",
                year = 2014,
                price = 29000,
                owner = owner2
            )
        )
        carRepository.save(
            Car(
                brand = "Ford",
                model = "Prius",
                color = "Silver",
                registerNumber = "KKO-0212",
                year = 2018,
                price = 39000,
                owner = owner2
            )
        )
        userRepository.save(User(
            userName = "user",
            password = "\$2a\$10\$1vUrQ1b.OgiT/LbK2WaUXe3JzYApRTALo9xpIB.tpaQyvLnWl8W6C",
            role = "USER")
        )
        userRepository.save(User(
            userName = "admin",
            password = "\$2a\$10\$JtIZXGEuPedNv1.MzFsumenqYmDLceO0KucKqGgKU/qnkV36qRicy",
            role = "ADMIN")
        )
    }
}

fun main(args: Array<String>) {
    runApplication<SpringReactApplication>(*args)
}
