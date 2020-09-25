package com.example.springreact.web

import com.example.springreact.domain.CarRepository
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class CarController(val carRepository: CarRepository) {

    @RequestMapping(path = ["/cars"], method = [RequestMethod.GET])
    fun cars() = carRepository.findAll()
}
