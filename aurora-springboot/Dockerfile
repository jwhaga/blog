FROM eclipse-temurin:25-jdk
RUN rm -f /etc/localtime \
    && ln -sv /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone
COPY target/aurora-springboot-0.0.1.jar /app/aurora-springboot-0.0.1.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/aurora-springboot-0.0.1.jar","--spring.profiles.active=prod"]
EXPOSE 8080
