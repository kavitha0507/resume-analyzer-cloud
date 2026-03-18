package com.kavitha.resume_analyzer;

// These are the correct imports from Spring, not your local folder!
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tika.Tika;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
 // Now it will find the correct Spring version
public class CareerCoachController {
    
    private final String GROQ_API_KEY = System.getenv("GROQ_API_KEY");

    @GetMapping("/")
    public String home() {
        return "AI Career Copilot Cloud Backend is running.";
    }

    @PostMapping("/analyze-resume")
    public String analyzeResume(
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "jd", required = false) String jobDescription
    ) {
        try {
            Tika tika = new Tika();
            String resumeContent = tika.parseToString(file.getInputStream());

            String prompt = "You are a Professional Resume Writer. Rewrite this resume for the JD: " + 
                            resumeContent + " \n\n JD: " + (jobDescription != null ? jobDescription : "General optimization");

            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("model", "llama-3.3-70b-versatile");
            bodyMap.put("messages", List.of(Map.of("role", "user", "content", prompt)));

            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(bodyMap);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + GROQ_API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();

        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }
}