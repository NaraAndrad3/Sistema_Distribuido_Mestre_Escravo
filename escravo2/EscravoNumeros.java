package escravo2;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class EscravoNumeros {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8082), 0);
        server.createContext("/numeros", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                byte[] input = exchange.getRequestBody().readAllBytes();
                String texto = new String(input);
                long count = texto.chars().filter(Character::isDigit).count();
                String resposta = String.valueOf(count);
                exchange.sendResponseHeaders(200, resposta.length());
                OutputStream os = exchange.getResponseBody();
                os.write(resposta.getBytes());
                os.close();
            }
        });
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("Servidor EscravoNumeros iniciado na porta 8082...");
    }
}
