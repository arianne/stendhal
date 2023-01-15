FROM eclipse-temurin:8-alpine
RUN mkdir -p /stendhal/log
COPY data /stendhal
COPY *.jar /stendhal
COPY start.sh /stendhal
VOLUME /stendhal/log
WORKDIR /stendhal
ENTRYPOINT /stendhal/start.sh