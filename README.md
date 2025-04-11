# Sistema Mestre-Escravo para Processamento de Texto

Este sistema demonstra uma arquitetura mestre-escravo distribuída para processar arquivos de texto. O cliente envia um arquivo para o mestre, que por sua vez distribui o processamento para dois escravos especializados (um para contar letras e outro para contar números). O mestre então agrega os resultados e os envia de volta ao cliente.

## Arquitetura

O sistema é composto por três componentes principais:

1.  **Cliente (Notebook 1):**
    * Uma aplicação gráfica Swing (`ClienteGUI.java`) que permite ao usuário selecionar um arquivo de texto.
    * Envia o conteúdo do arquivo via requisição HTTP `POST` para o servidor mestre.
    * Exibe o resultado (contagem de letras e números) recebido do mestre.

2.  **Mestre (Servidor - Container Docker):**
    * Um servidor HTTP (`MestreServer.java`) rodando em um container Docker.
    * Recebe o arquivo do cliente via requisição HTTP `POST` no endpoint `/processar`.
    * Verifica a disponibilidade dos servidores escravos usando requisições HTTP `HEAD`.
    * Envia o conteúdo do arquivo para os escravos via requisições HTTP `POST`.
    * Espera pelas respostas dos escravos (contagem de letras e números).
    * Agrega os resultados e envia uma resposta HTTP `200` de volta ao cliente.

3.  **Escravos (Servidores - Containers Docker):**
    * Dois servidores HTTP distintos, cada um rodando em seu próprio container Docker, especializados em:
        * Contagem de letras (`Slave1Server.java`).
        * Contagem de números (`Slave2Server.java`).
    * Recebem o texto via requisição HTTP `POST`.
    * Realizam a contagem específica e retornam o resultado como resposta HTTP `200`.
    * Respondem a requisições `HEAD` com status `200` para indicar disponibilidade.

## Tecnologias Utilizadas

* **Linguagem de Programação:**
    * **Java:** Utilizada para desenvolver o cliente e todos os servidores (mestre e escravos).

* **Frameworks e APIs Java:**
    * **Swing (javax.swing):** Utilizado para criar a interface gráfica do cliente.
    * **Java NIO (java.nio.file):** Utilizado no cliente para ler o conteúdo dos arquivos.
    * **Java Net HTTP Client (java.net.http):** Utilizado no cliente para realizar requisições HTTP ao mestre.
    * **Sun HTTP Server (com.sun.net.httpserver):** Utilizado para implementar os servidores HTTP do mestre e dos escravos.
    * **Java Concurrency Utilities (java.util.concurrent):** Utilizado no mestre para gerenciar threads e esperar pelas respostas dos escravos.

* **Containerização:**
    * **Docker:** Utilizado para empacotar e executar os servidores mestre e escravos em containers isolados.

* **Orquestração de Containers:**
    * **Docker Compose:** Utilizado para definir e gerenciar a execução dos múltiplos containers (mestre e escravos) de forma orquestrada.

* **Protocolo de Comunicação:**
    * **HTTP (Hypertext Transfer Protocol):** Utilizado para toda a comunicação entre o cliente e o mestre, e entre o mestre e os escravos.

## Pré-requisitos

* **Java Development Kit (JDK):** Necessário para compilar e executar o código Java do cliente e dos servidores.
* **Docker:** Necessário para construir e executar os containers para os servidores mestre e escravos.
* **Docker Compose:** Recomendado para orquestrar a construção e execução dos múltiplos containers.

## Como Executar

Siga estas instruções para executar o sistema:

**1. Configurar o Ambiente Docker (Notebook 2):**

* Certifique-se de ter o Docker e o Docker Compose instalados.
* Navegue até o diretório raiz do projeto (`Trabalho04`).
* Execute o seguinte comando para construir e iniciar os containers do mestre e dos escravos:

    ```bash
    docker-compose up --build -d
    ```

    Este comando irá:
    * Construir as imagens Docker para o mestre e os escravos usando os Dockerfiles fornecidos.
    * Iniciar os containers em segundo plano.

* Verifique se os containers estão rodando com o comando:

    ```bash
    docker ps
    ```

    Você deverá ver os containers `trabalho04-mestre-1`, `trabalho04-escravo1-1` e `trabalho04-escravo2-1` com status `Up`.

**2. Executar o Cliente (Notebook 1):**

* Navegue até o diretório `cliente` dentro do projeto.
* Compile o código-fonte do cliente:

    ```bash
    javac ClienteGUI.java
    ```

* Execute a aplicação cliente:

    ```bash
    java cliente.ClienteGUI
    ```

    Isso abrirá a interface gráfica do cliente.

**3. Interagir com o Sistema:**

* Na interface do cliente, clique no botão "Enviar Arquivo".
* Selecione um arquivo de texto na janela de diálogo.
* O cliente enviará o conteúdo do arquivo para o servidor mestre.
* O mestre processará a requisição, comunicando-se com os escravos.
* O resultado (contagem de letras e números) será exibido na área de texto do cliente.

## Docker Compose (`docker-compose.yml`)

O arquivo `docker-compose.yml` define os serviços para o mestre e os escravos:

```yaml
version: "3.8"

services:
  mestre:
    build: ./mestre
    ports:
      - "8080:8080"
    depends_on:
      - escravo1
      - escravo2

  escravo1:
    build: ./escravo1
    ports:
      - "8081:8081"
    healthcheck:
      test: ["CMD", "curl", "-f", "-I", "http://localhost:8081/letras"]
      interval: 5s
      timeout: 3s
      start_period: 5s

  escravo2:
    build: ./escravo2
    ports:
      - "8082:8082"
    healthcheck:
      test: ["CMD", "curl", "-f", "-I", "http://localhost:8082/numeros"]
      interval: 5s
      timeout: 3s
      start_period: 5s