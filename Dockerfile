FROM eclipse-temurin:21-jre
WORKDIR /app
COPY moneymanager/target/moneymanager-0.0.1-SNAPSHOT.jar moneymamager-v1.0.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "moneymamager-v1.0.jar"]