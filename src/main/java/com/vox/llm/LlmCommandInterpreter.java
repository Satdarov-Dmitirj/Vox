package com.vox.llm;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class LlmCommandInterpreter {
    private final HttpClient client = HttpClient.newHttpClient();
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";

    private static final String COMMAND_PROMPT = """
            Ты — парсер голосовых команд для компьютера.
            Верни ТОЛЬКО одно слово — действие, без пояснений и без кавычек.
            Доступные действия: open_downloads, open_documents, open_desktop, open_pictures,
            open_browser, open_notepad, open_calculator, open_explorer,
            shutdown, restart, lock, unknown.
            Если фраза не подходит ни под одно действие — верни unknown.

            Фраза: "%s"
            Действие:""";

    private static final String CHAT_PROMPT = """
            Ты — голосовой ассистент по имени Вокс. Отвечай кратко (1-3 предложения),
            дружелюбно, на русском языке, без markdown-разметки — ответ будет озвучен голосом.

            Вопрос: "%s"
            Ответ:""";

    public String interpret(String userPhrase) {
        String raw = callOllama(COMMAND_PROMPT.formatted(userPhrase));
        return cleanAction(raw);
    }

    public String chat(String userPhrase) {
        String raw = callOllama(CHAT_PROMPT.formatted(userPhrase));
        return raw.trim();
    }

    private String callOllama(String prompt) {
        try {
            String jsonBody = """
                    {"model": "llama3.2", "prompt": %s, "stream": false, "keep_alive": "30m"}
                    """.formatted(quoteJson(prompt));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OLLAMA_URL))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return extractField(response.body(), "response");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
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