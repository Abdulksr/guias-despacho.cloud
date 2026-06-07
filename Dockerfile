FROM eclipse-temurin:25-jdk AS buildstage 

RUN apt-get update && apt-get install -y maven

WORKDIR /app

COPY pom.xml .
COPY src /app/src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:25-jdk

COPY --from=buildstage /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
