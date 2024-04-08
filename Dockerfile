FROM openjdk:21

WORKDIR /app

COPY build/libs/transactionmanager-1.0.jar /app

EXPOSE 8080

CMD ["java", "-jar", "transactionmanager-1.0.jar"]
