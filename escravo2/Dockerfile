FROM openjdk:17
WORKDIR /app
RUN mkdir -p escravo2
COPY EscravoNumeros.java escravo2/
RUN javac escravo2/EscravoNumeros.java
EXPOSE 8082
HEALTHCHECK --interval=5s --timeout=3s CMD curl -f -I http://localhost:8082/numeros
CMD ["java", "escravo2.EscravoNumeros"]