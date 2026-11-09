package com.desafio.livros.controller;

import com.desafio.livros.model.Livro;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LivroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private long cadastrarLivro(String titulo) throws Exception {
        MvcResult result = mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Livro(titulo, "Autor Teste", 2001))))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    void deveCadastrarLivro() throws Exception {
        mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new Livro("O Hobbit", "J.R.R. Tolkien", 1937))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.titulo").value("O Hobbit"))
                .andExpect(jsonPath("$.autor").value("J.R.R. Tolkien"))
                .andExpect(jsonPath("$.anoPublicacao").value(1937));
    }

    @Test
    void deveListarLivros() throws Exception {
        cadastrarLivro("Livro para listar");

        mockMvc.perform(get("/livros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void deveAtualizarLivro() throws Exception {
        long id = cadastrarLivro("Título antigo");

        mockMvc.perform(put("/livros/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new Livro("Título novo", "Novo Autor", 2024))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.titulo").value("Título novo"))
                .andExpect(jsonPath("$.autor").value("Novo Autor"))
                .andExpect(jsonPath("$.anoPublicacao").value(2024));
    }

    @Test
    void deveRetornar404AoAtualizarLivroInexistente() throws Exception {
        mockMvc.perform(put("/livros/{id}", 999999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Livro("X", "Y", 2000))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveExcluirLivro() throws Exception {
        long id = cadastrarLivro("Livro para excluir");

        mockMvc.perform(delete("/livros/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/livros/{id}", id))
                .andExpect(status().isNotFound());
    }
}
