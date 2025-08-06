FROM gradle:8.8.0-jdk21 AS builder

WORKDIR /app

COPY . .

RUN gradle installDist

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/build/install/app /app

CMD ["./bin/app"]