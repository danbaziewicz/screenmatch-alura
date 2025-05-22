package br.com.alura.screenmatch.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApiKeyLoader {
    public static String loadApiKey() {
        Properties props = new Properties();
        try (InputStream input = ApiKeyLoader.class.getClassLoader().getResourceAsStream("api-key.properties")) {
            if (input == null) {
                throw new IOException("Arquivo api-key.properties não encontrado");
            }
            props.load(input);
            return props.getProperty("API_KEY");
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar a chave da API", e);
        }
    }
}
