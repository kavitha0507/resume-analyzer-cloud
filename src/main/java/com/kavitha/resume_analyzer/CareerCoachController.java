package com.kavitha.resume_analyzer;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/")
public class CareerCoachController {

    // This pulls the API Key you saved in the Render "Environment" tab
    private final String GROQ_API_KEY = System.getenv("GROQ_API_KEY");

    @PostMapping("/analyze-resume")
    public ResponseEntity<String> analyzeResume(
            @RequestParam("file") MultipartFile file, 
            @RequestParam("jobDescription") String jobDescription) {
        
        try {
            // 1. Extract text from the PDF/Docx using Tika
            Tika tika = new Tika();
            String resumeContent = tika.parseToString(file.getInputStream());

            // 2. Prepare the AI Prompt
            String prompt = "You are a Professional Resume Writer. Rewrite this resume for the JD: " + 
                            resumeContent + " \n\n JD: " + (jobDescription != null ? jobDescription : "General optimization");

            // 3. Build the JSON body for Groq
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("model", "llama-3.3-70b-versatile");
            bodyMap.put("messages", List.of(Map.of("role", "user", "content", prompt)));

            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(bodyMap);

            // 4. Send the request to Groq API
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + GROQ_API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            // 5. Return the AI response to your React frontend
            return ResponseEntity.ok(response.body());

        } catch (Exception e) {
            // Returns a clean JSON error if something goes wrong
            return ResponseEntity.status(500).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}