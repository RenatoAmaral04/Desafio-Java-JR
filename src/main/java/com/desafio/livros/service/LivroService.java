package com.desafio.livros.service;

import com.desafio.livros.model.Livro;
import com.desafio.livros.repository.LivroRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LivroService {

    private final LivroRepository repository;

    public LivroService(LivroRepository repository) {
        this.repository = repository;
    }

    public Livro cadastrar(Livro livro) {
        livro.setId(null); 
        return repository.save(livro);
    }

    public List<Livro> listar() {
        return repository.findAll();
    }

    public Optional<Livro> atualizar(Long id, Livro dados) {
        return repository.findById(id).map(livro -> {
            livro.setTitulo(dados.getTitulo());
            livro.setAutor(dados.getAutor());
            livro.setAnoPublicacao(dados.getAnoPublicacao());
            return repository.save(livro);
        });
    }

    public boolean excluir(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }
}
