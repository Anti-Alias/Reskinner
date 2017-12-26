# Software Requirements
1) Postgres 10.1
2) Gradle 4.4.1
3) JDK 8 (With path variables set to JDK)

# Build Instructions

### Locally
```groovy
./gradlew run
```

### With Docker
```bash
./test-docker
```

# Docker Build Instructions
```
docker-compose up
```

# Local URLs

### Reskinner
1. Windows: http://192.168.99.100:8080
2. Linux: http://localhost:8080

### Adminer
1) Windows: http://192.168.99.100:8081
2) Linux: http://localhost:8081

### Postgres
1) Windows: 192.0.0.1:5432
2) Linux: localhost:5432