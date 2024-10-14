# Etapa de construcción
FROM maven:3.8.4-openjdk-17 AS build

# Establece el directorio de trabajo
WORKDIR /app

# Copia los archivos del proyecto
COPY pom.xml .
COPY src ./src

# Construye el proyecto con Maven
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM openjdk:17-jdk-slim

# Establece el directorio de trabajo
WORKDIR /app

# Copia el archivo JAR construido desde la etapa de construcción
COPY --from=build /app/target/Proyecto-Integrador-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto que utilizará la aplicación
EXPOSE 8080

# Define el comando de inicio de la aplicación
CMD ["java", "-jar", "app.jar"]
