package com.vox.llm;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class LlmCommandInterpreter {
    private final HttpClient client = HttpClient.newHttpClient();
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";

    private static final String SYSTEM_PROMPT = """
            Ты — парсер голосовых команд для компьютера.
            Верни ТОЛЬКО одно слово — действие, без пояснений и без кавычек.
            Доступные действия: open_downloads, open_documents, open_desktop, open_pictures,
            open_browser, open_notepad, open_calculator, open_explorer,
            shutdown, restart, lock, unknown.
            Если фраза не подходит ни под одно действие — верни unknown.

            Фраза: "%s"
            Действие:""";

    public String interpret(String userPhrase) {
        try {
            String prompt = SYSTEM_PROMPT.formatted(userPhrase);
            String jsonBody = """
                    {"model": "llama3.2", "prompt": %s, "stream": false}
                    """.formatted(quoteJson(prompt));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OLLAMA_URL))
                    .timeout(Duration.ofSeconds(15))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String rawAnswer = extractField(response.body(), "response");
            return cleanAction(rawAnswer);
        } catch (Exception e) {
            e.printStackTrace();
            return "unknown";
        }
    }

    private String cleanAction(String raw) {
        return raw.trim().toLowerCase().replaceAll("[^a-z_]", "");
    }

    private String quoteJson(String text) {
        String escaped = text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
        return "\"" + escaped + "\"";
    }

    private String extractField(String json, String key) {
        String search = "\"" + key + "\"";
        int keyIdx = json.indexOf(search);
        if (keyIdx < 0) return "";
        int colonIdx = json.indexOf(':', keyIdx + search.length());
        if (colonIdx < 0) return "";
        int firstQuote = json.indexOf('"', colonIdx);
        if (firstQuote < 0) return "";
        int secondQuote = json.indexOf('"', firstQuote + 1);
        while (secondQuote > 0 && json.charAt(secondQuote - 1) == '\\') {
            secondQuote = json.indexOf('"', secondQuote + 1);
        }
        if (secondQuote < 0) return "";
        return json.substring(firstQuote + 1, secondQuote).replace("\\n", "\n");
    }
}