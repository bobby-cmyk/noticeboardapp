FROM eclipse-temurin:23-noble AS builder

WORKDIR /src

# copy files
COPY mvnw .
COPY pom.xml .

COPY .mvn .mvn
COPY src src

# make mvnw executable
RUN chmod a+x mvnw && /src/mvnw package -Dmaven.test.skip=true

FROM eclipse-temurin:23-jre-noble

WORKDIR /app

COPY --from=builder /src/target/noticeboard-0.0.1-SNAPSHOT.jar app.jar

# check if curl command is available
RUN apt update && apt install -y curl

ENV PORT=8080
ENV NOTICEBOARD_DB_HOST=localhost
ENV NOTICEBOARD_DB_PORT=6379
ENV NOTICEBOARD_DB_DATABASE=0
ENV NOTICEBOARD_DB_USERNAME=""
ENV NOTICEBOARD_DB_PASSWORD=""
ENV NOTICE_SERVER_URL=https://publishing-production-d35a.up.railway.app/

EXPOSE ${PORT}

HEALTHCHECK --interval=60s --start-period=120s\
   CMD curl -s -f http://localhost:${PORT}/status || exit 1

ENTRYPOINT SERVER_PORT=${PORT} java -jar app.jar