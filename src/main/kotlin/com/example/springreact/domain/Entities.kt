package com.example.springreact.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
class Car(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var brand: String,
    var model: String,
    var color: String,
    var registerNumber: String?,
    var year: Int,
    var price: Int,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner")
    var owner: Owner? = null
)

@Entity
class Owner(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var firstName: String,
    var lastName: String,
    @JsonIgnore
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "owner")
    var cars: List<Car> = mutableListOf()
)
