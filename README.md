# Teste Técnico: Backend Software Developer

Este projeto foi desenvolvido como parte de um teste técnico para implementar uma API HTTP para busca de URLs que contenham um termo fornecido pelo usuário. A aplicação utiliza uma arquitetura hexagonal, permitindo maior testabilidade e manutenibilidade.

---

## 🎯 **Objetivo**

Desenvolver uma aplicação Java para navegar por um website e listar as URLs que contenham um termo fornecido pelo usuário. O sistema suporta múltiplas buscas simultâneas e retorna resultados parciais enquanto a busca está em andamento.

---

## 🚀 **Como executar a aplicação**

### **Pré-requisitos**

- **Docker** instalado na máquina
- **Variável de ambiente** `BASE_URL` configurada para determinar a URL base da busca de links.


### **Passos para subir a aplicação**

1. Compile a imagem Docker:

```bash
docker build . -t axreng/backend
```

2. Execute o container Docker:

```bash
docker run -e BASE_URL=http://hiring.axreng.com/ -p 4567:4567 --rm axreng/backend
```


A aplicação estará disponível na URL: `http://localhost:4567`.

---

## 📚 **Estrutura do Projeto**

A aplicação segue a arquitetura hexagonal:

```
com.axreng.backend/
├── domain/
│   ├── model/             
│   │   ├── CrawlJob.java         # Representa as características de uma busca
│   │   ├── CrawlStatus.java      # Enum de status ("active" ou "done")
│   ├── port/                    
│       ├── CrawlerService.java   # Porta para execução de crawling
│       ├── CrawlRepository.java  # Porta para persistência dos jobs
├── application/
│   ├── CrawlServiceImpl.java     # Implementa os casos de uso do domínio
├── adapter/
│   ├── in/
│   │   ├── web/
│   │   │   ├── CrawlController.java # Controla as rotas da API
│   │   │   ├── dto/
│   │   │       ├── CrawlRequest.java  # Representação da requisição POST
│   │   │       ├── CrawlResponse.java # Representação da resposta GET
│   ├── out/
│       ├── crawler/
│       │   ├── WebCrawlerImpl.java    # Implementação do mecanismo de busca
│       ├── persistence/
│           ├── InMemoryCrawlRepository.java # Repositório de busca em memória
├── config/
│   ├── SparkConfig.java             # Configuração do servidor Spark
├── Main.java                        # Ponto de entrada da aplicação
└── test/
    ├── domain/
    │   ├── CrawlJobTest.java        # Testes unitários para o modelo de domínio
    ├── application/
    │   ├── CrawlServiceImplTest.java # Testes unitários para os casos de uso
    ├── adapter/
    │   ├── in/
    │   │   ├── web/
    │   │       ├── CrawlControllerTest.java # Testes para o controlador da API
    │   ├── out/
    │       ├── crawler/
    │           ├── WebCrawlerImplTest.java # Testes para o mecanismo de busca
```

---

## ✨ **Principais Funcionalidades**

1. **POST `/crawl`**:
    - Inicia uma nova busca com o termo fornecido.
    - **Requisição**:

```json
{
  "keyword": "security"
}
```

    - **Resposta**:

```json
{
  "id": "abc12345"
}
```

2. **GET `/crawl/:id`**:
    - Retorna o status da busca e as URLs encontradas até o momento.
    - **Resposta**:

```json
{
  "id": "abc12345",
  "status": "active",
  "urls": [
    "http://hiring.axreng.com/index2.html",
    "http://hiring.axreng.com/htmlman1/chcon.1.html"
  ]
}
```

3. Suporte a **múltiplas buscas simultâneas**.
4. Retorno de **resultados parciais** durante a execução da busca.
5. **Execução paralela** controlada por variável de ambiente (`MULTITHREAD_OPT`).

---

## 🔧 **Tecnologias Utilizadas**

- **Java 11**: Linguagem principal para implementação.
- **Maven**: Gerenciador de dependências e build.
- **Spark Java**: Framework para a API HTTP.
- **Gson**: Biblioteca para serialização/deserialização JSON.
- **JUnit 5**: Framework para testes unitários.
- **Mockito**: Mocking para simular dependências nos testes.

---

## ⚙️ **Variáveis de Ambiente**

- `BASE_URL`: Define a URL base onde as buscas serão feitas.
    - Valor padrão: `http://hiring.axreng.com/`
- `MULTITHREAD_OPT`: Define se a busca será feita em paralelo.
    - Valores aceitos: `"true"` ou `"false"`
    - Valor padrão: `"false"`

---

## ✅ **Testes Implementados**

Os testes cobrem todas as camadas da aplicação:

1. **Domínio (`CrawlJobTest`)**:
    - Validação da criação do job e sua manipulação (como adicionar URLs encontradas).
2. **Aplicação (`CrawlServiceImplTest`)**:
    - Testa os casos de uso para iniciar buscas e consultar jobs existentes.
3. **Adaptadores de Entrada (API, `CrawlControllerTest`)**:
    - Verifica o comportamento esperado dos endpoints POST `/crawl` e GET `/crawl/:id`.
4. **Adaptadores de Saída (`InMemoryCrawlRepositoryTest`)**:
    - Valida o repositório em memória, garantindo persistência correta.
5. **WebCrawler (`WebCrawlerImplTest`)**:
    - Testa a lógica de resolução de URLs e extração de links.

---

## 📊 **Avaliando o Desempenho**

- Busca paralela otimizada com `ExecutorService`.
- URLs visitadas são armazenadas em uma estrutura thread-safe (`ConcurrentHashMap` ou `synchronizedSet`).
- Limite de 100 resultados por busca, conforme especificado no teste.

---

## 📝 **Observações**

1. Os arquivos `pom.xml` e `Dockerfile` **não foram modificados**, conforme solicitado no teste técnico.
2. A aplicação atende a todos os requisitos funcionais descritos, incluindo:
    - Validação do `keyword` (mínimo 4, máximo 32 caracteres).
    - Respeito à URL base, seguindo apenas links do mesmo domínio.
    - Retorno parcial de resultados pela API enquanto a busca ainda está em andamento.
3. A solução foi orientada para ser testável, escalável e simples de manter.

---

💡 **Dúvidas ou sugestões?** Sinta-se à vontade para entrar em contato! 😊

---

Salve este conteúdo como um arquivo `.md` e será renderizado corretamente com qualquer visualizador Markdown. Caso precise de mais ajustes, é só avisar! 😊

<div>⁂</div>

[^1]: https://pplx-res.cloudinary.com/image/upload/v1743978412/user_uploads/IvWPLwtDoccAUPR/image.jpg

