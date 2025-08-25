# Giai đoạn 1: Build ứng dụng với Maven và JDK 17
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Giai đoạn 2: Chạy ứng dụng trên một môi trường Java nhẹ hơn
FROM openjdk:17-jdk-slim
WORKDIR /app
# Sao chép file .jar đã được build từ giai đoạn 1
COPY --from=build /app/target/bookRead-0.0.1-SNAPSHOT.jar app.jar
# Cổng mặc định của Spring Boot là 8080
EXPOSE 8080
# Lệnh để chạy ứng dụng
ENTRYPOINT ["java","-jar","app.jar"]