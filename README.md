# Desafio Java Jr - API de Livros

API REST para gerenciar livros (cadastrar, listar, atualizar e excluir), feita com Spring Boot e banco H2 em memória.

## Como executar

**Requisitos:** Java 17+ e Maven 3.8+.

```bash
mvn spring-boot:run   # sobe a API em http://localhost:8080
mvn test              # executa os testes
```

Os dados ficam em memória: ao reiniciar a aplicação, a lista de livros volta vazia.

## Modelo de dados

| Campo           | Tipo      | Descrição                                           |
|-----------------|-----------|-----------------------------------------------------|
| `id`            | `Long`    | Identificador único, gerado automaticamente pelo banco |
| `titulo`        | `String`  | Título do livro                                     |
| `autor`         | `String`  | Autor do livro                                      |
| `anoPublicacao` | `Integer` | Ano de publicação                                   |

## Documentação das rotas

Base URL: `http://localhost:8080`

| Método | Rota           | O que faz                          | Sucesso          | Erro               |
|--------|----------------|------------------------------------|------------------|--------------------|
| POST   | `/livros`      | Cadastra um novo livro             | `201 Created`    | -                  |
| GET    | `/livros`      | Lista todos os livros cadastrados  | `200 OK`         | -                  |
| PUT    | `/livros/{id}` | Atualiza os dados de um livro      | `200 OK`         | `404 Not Found`    |
| DELETE | `/livros/{id}` | Exclui um livro                    | `204 No Content` | `404 Not Found`    |

### POST /livros - Cadastrar livro

Cria um novo livro. O `id` é gerado pelo banco; se for enviado no corpo, é ignorado.

**Corpo da requisição**

```json
{
  "titulo": "Dom Casmurro",
  "autor": "Machado de Assis",
  "anoPublicacao": 1899
}
```

**Resposta: `201 Created`**

```json
{
  "id": 1,
  "titulo": "Dom Casmurro",
  "autor": "Machado de Assis",
  "anoPublicacao": 1899
}
```

### GET /livros - Listar livros

Retorna todos os livros cadastrados. Se não houver nenhum, retorna uma lista vazia (`[]`).

**Resposta: `200 OK`**

```json
[
  {
    "id": 1,
    "titulo": "Dom Casmurro",
    "autor": "Machado de Assis",
    "anoPublicacao": 1899
  }
]
```

### PUT /livros/{id} - Atualizar livro

Atualiza título, autor e ano de publicação do livro identificado pelo `id` da URL.

**Parâmetro de rota:** `id` - identificador do livro a atualizar.

**Corpo da requisição**

```json
{
  "titulo": "Dom Casmurro",
  "autor": "Machado de Assis",
  "anoPublicacao": 1900
}
```

**Respostas**

- `200 OK`: devolve o livro atualizado (mesmo formato do POST).
- `404 Not Found`: não existe livro com o `id` informado (resposta sem corpo).

### DELETE /livros/{id} - Excluir livro

Remove o livro identificado pelo `id` da URL.

**Parâmetro de rota:** `id` - identificador do livro a excluir.

**Respostas**

- `204 No Content`: livro excluído (resposta sem corpo).
- `404 Not Found`: não existe livro com o `id` informado (resposta sem corpo).

### Exemplos com curl

```bash
# Cadastrar
curl -X POST http://localhost:8080/livros \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Dom Casmurro","autor":"Machado de Assis","anoPublicacao":1899}'

# Listar
curl http://localhost:8080/livros

# Atualizar o livro de id 1
curl -X PUT http://localhost:8080/livros/1 \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Dom Casmurro","autor":"Machado de Assis","anoPublicacao":1900}'

# Excluir o livro de id 1
curl -X DELETE http://localhost:8080/livros/1
```

## Decisões técnicas

| Tecnologia | Por que foi escolhida |
|------------|-----------------------|
| **Java 17 + Spring Boot 3** | Padrão de mercado para APIs REST. Servidor embutido e auto-configuração permitem uma API funcional com pouco código. |
| **Spring Web** | Criação das rotas de forma declarativa (`@RestController`, `@GetMapping` etc.) e conversão automática para JSON. |
| **Spring Data JPA** | O `JpaRepository` já traz o CRUD pronto, sem escrever SQL, e facilita trocar o banco depois. |
| **H2 em memória** | Banco simples pedido no desafio; não exige instalação. Para produção, bastaria trocar a dependência e a URL por PostgreSQL ou MySQL. |
| **Camadas (controller, service, repository)** | Cada classe tem uma responsabilidade: o controller trata o HTTP, o service as regras, o repository o banco. Facilita leitura e manutenção. |
| **JUnit 5 + MockMvc** | Testam as rotas simulando requisições HTTP, sem precisar abrir uma porta de rede. |
| **Maven** | Gerencia dependências e permite rodar e testar com um único comando. |

## Estrutura do projeto

```
src/main/java/com/desafio/livros/
├── LivrosApiApplication.java      # ponto de entrada
├── controller/LivroController.java  # rotas HTTP
├── service/LivroService.java        # regras da aplicação
├── repository/LivroRepository.java  # acesso ao banco
└── model/Livro.java                 # entidade
src/test/java/com/desafio/livros/
└── controller/LivroControllerTest.java  # testes das rotas
```

## Testes

Os testes em `LivroControllerTest` usam JUnit 5 e MockMvc e cobrem:

- cadastro de livro (`201`);
- listagem de livros (`200`);
- atualização de livro existente (`200`) e inexistente (`404`);
- exclusão de livro existente (`204`) e inexistente (`404`).

Cada teste cadastra seus próprios dados, sem depender da ordem de execução.

## Possíveis melhorias

- Validação dos campos (título e autor obrigatórios) com Bean Validation, retornando `400`.
- Rota `GET /livros/{id}` para consultar um livro específico.
- Banco de dados persistente (PostgreSQL) com migrações (Flyway).
- Paginação na listagem e documentação interativa com Swagger/OpenAPI.
