package com.example.springreact.domain

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface CarRepository : CrudRepository<Car, Long> {
    fun findByBrand(brand: String): List<Car>

    fun findByColor(color: String): List<Car>

    fun findByYear(year: Int): List<Car>

    fun findByBrandAndModel(brand: String, model: String): List<Car>

    fun findByBrandOrderByYearAsc(brand: String): List<Car>

    @Query("select c from Car c where c.brand like %?1")
    fun findByBrandEndsWith(brand: String)
}

interface OwnerRepository : CrudRepository<Owner, Long>
