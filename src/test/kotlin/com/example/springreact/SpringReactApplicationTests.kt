package com.example.springreact

import com.example.springreact.web.CarController
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SpringReactApplicationTests(
    @Autowired val carController: CarController
) {

    @Test
    fun `Context Loads`() {
        assertThat(carController).isNotNull
    }

}
