import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class EscravoLetras {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        server.createContext("/letras", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                byte[] input = exchange.getRequestBody().readAllBytes();
                String texto = new String(input);
                long count = texto.chars().filter(Character::isLetter).count();
                String resposta = String.valueOf(count);
                exchange.sendResponseHeaders(200, resposta.length());
                exchange.getResponseBody().write(resposta.getBytes());
                exchange.close();
            }
        });
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("Servidor EscravoLetras iniciado na porta 8081...");
    }
}