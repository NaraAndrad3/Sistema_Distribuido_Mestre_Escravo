FROM openjdk:17
WORKDIR /app
COPY EscravoLetras.java .
RUN javac EscravoLetras.java
EXPOSE 8081
HEALTHCHECK --interval=5s --timeout=3s CMD curl -f -I http://localhost:8081/letras || exit 1
CMD ["java", "EscravoLetras"]