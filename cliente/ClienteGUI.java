package cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClienteGUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Cliente Distribuído");
        JButton button = new JButton("Enviar Arquivo");
        JTextArea resultArea = new JTextArea(10, 40);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Definindo a cor rosa para o background
        Color backgroundColor = Color.PINK;
        Color textColor = Color.BLACK;
        Color buttonColor = new Color(255, 204, 204); // Rosa claro para o botão

        // Aplicando a cor rosa ao frame (painel de conteúdo)
        frame.getContentPane().setBackground(backgroundColor);

        // Aplicando a cor ao botão
        button.setBackground(buttonColor);
        button.setForeground(textColor);

        // Aplicando as cores à área de texto
        resultArea.setBackground(Color.WHITE);
        resultArea.setForeground(textColor);

        button.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    byte[] fileBytes = new FileInputStream(file).readAllBytes();
                    HttpURLConnection con = (HttpURLConnection) new URL("http://10.180.46.158:8080/processar").openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    con.getOutputStream().write(fileBytes);
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line;
                    resultArea.setText("");
                    while ((line = in.readLine()) != null) resultArea.append(line + "\n");
                    in.close();
                } catch (Exception ex) {
                    resultArea.setText("Erro: " + ex.getMessage());
                }
            }
        });

        frame.setLayout(new FlowLayout());
        frame.add(button);
        frame.add(scrollPane);
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

// 10.180.46.158