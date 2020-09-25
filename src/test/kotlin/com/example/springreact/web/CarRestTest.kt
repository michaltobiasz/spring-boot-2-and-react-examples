package com.example.springreact.web

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest
@AutoConfigureMockMvc
class CarRestTest(
    @Autowired val mockMvc: MockMvc
) {
    @Test
    fun `Testing authentication with correct credentials`() {
        mockMvc
            .perform(
                post("/login").content("{\"username\":\"admin\", \"password\":\"admin\"}")
            )
            .andDo(print())
            .andExpect(status().isOk)
    }

    @Test
    fun `Testing authentication with wrong credentials`() {
        mockMvc
            .perform(
                post("/login").content("{\"username\":\"admin\", \"password\":\"wrongpwd\"}")
            )
            .andDo(print())
            .andExpect(status().is4xxClientError)
    }
}
