import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class MestreServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/processar", new MestreHandler());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("SEvidor Mestre  iniciado na porta 8080...");
    }
}

class MestreHandler implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {
        byte[] input = exchange.getRequestBody().readAllBytes();
        String texto = new String(input);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<Integer> letras = executor.submit(() -> processar("http://escravo1:8081/letras", texto));
        Future<Integer> numeros = executor.submit(() -> processar("http://escravo2:8082/numeros", texto));

        try {
            int countLetras = letras.get();
            int countNumeros = numeros.get();
            String resultado = "Letras: " + countLetras + ", Números: " + countNumeros;
            exchange.sendResponseHeaders(200, resultado.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(resultado.getBytes());
            os.close();
        } catch (Exception e) {
            String erro = "Erro ao processar: " + e.getMessage();
            exchange.sendResponseHeaders(500, erro.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(erro.getBytes());
            os.close();
        }
    }

    private int processar(String urlStr, String texto) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(urlStr).openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.getOutputStream().write(texto.getBytes());
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        return Integer.parseInt(in.readLine());
    }
}