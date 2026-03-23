🏗️ System Architecture
To ensure high availability and performance, this project utilizes a Decoupled Full-Stack Architecture, separating the user interface from the heavy processing logic.

1. Frontend: Vercel (The Presentation Layer)
Technology: Next.js / React

Role: Hosts the responsive UI and handles client-side form validation.

Why Vercel? Chosen for its Edge Network capabilities, ensuring the global delivery of static assets (HTML/CSS/JS) with minimal latency. It provides a seamless user experience for the initial page load.

2. Backend: Render (The Logic Layer)
Technology: Java 21 / Spring Boot

Role: Powers the "Brain" of the application. It handles PDF/JSON parsing, communicates with the LLM for analysis, and manages data persistence.

Why Render? Unlike serverless environments, Render provides a persistent runtime for the Spring Boot JVM. This allows the backend to handle long-running AI processing tasks and maintain stable database connections without the timeout limits found in serverless platforms.

3. Data Flow
User uploads a resume via the Vercel frontend.

The frontend initiates a RESTful API call to the Render backend.

The Spring Boot server processes the request, generates AI-driven tips, and sends a JSON response back.

The frontend dynamically updates the dashboard with the analysis.
