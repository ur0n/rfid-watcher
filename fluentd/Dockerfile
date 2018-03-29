FROM fluent/fluentd

COPY ./fluentd/conf  /fluentd/etc

RUN ["gem", "install", "fluent-plugin-redis", "--no-rdoc", "--no-ri", "--version", "0.3.3"]
