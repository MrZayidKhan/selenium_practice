# Stage 1: Build the Maven project
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy project files
COPY pom.xml .
COPY testng.xml .
COPY webpage/ webpage/
COPY src/ src/

# Build project (optional: run tests during build or skip if already tested in GitHub Actions)
RUN mvn clean package -DskipTests

# Stage 2: Runtime Image with Java & Chrome
FROM eclipse-temurin:17-jre-jammy

# Install Google Chrome dependencies and Chrome stable browser
RUN apt-get update && apt-get install -y \
    wget \
    gnupg \
    unzip \
    curl \
    libxi6 \
    libgconf-2-4 \
    libnss3 \
    libxss1 \
    libasound2 \
    fonts-liberation \
    xdg-utils \
    && wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | gpg --dearmor -o /usr/share/keyrings/google-chrome.gpg \
    && echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-chrome.gpg] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy built jar/target artifacts and test resources from the builder stage
COPY --from=builder /app/webpage /app/webpage
COPY --from=builder /app/target /app/target
COPY --from=builder /app/testng.xml /app/testng.xml

# Default command to run tests inside the container if invoked directly
CMD ["mvn", "test"]