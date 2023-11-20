#FROM maven:3.9.5-amazoncorretto-21
#
#WORKDIR /app
#
#COPY target/TelegramBot-0.0.1-SNAPSHOT.jar app.jar
#
#EXPOSE 8080
#
#CMD ["java", "-jar", "--enable-preview", "app.jar"]



# Этап сборки
FROM maven:3.9.5-amazoncorretto-21 AS builder

WORKDIR /app

# Копируем файлы с зависимостями проекта и запускаем сборку
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests

# Этап запуска
FROM openjdk:21-jdk

WORKDIR /app

# Копируем собранный JAR файл из этапа сборки в финальный образ
COPY --from=builder /app/target/TelegramBot-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "--enable-preview", "app.jar"]