#
# The MIT License (MIT)
#
# Copyright (c) 2017 Johannes Schnatterer
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.
#

# Define maven version for all stages
FROM maven:3.6.0-jdk-8-alpine as maven-git
# Install git in order to be able to write version info during maven build
RUN apk --update add git && \
    rm -rf /var/lib/apt/lists/* && \
    rm /var/cache/apk/*

FROM maven-git as mavencache
ENV MAVEN_OPTS=-Dmaven.repo.local=/mvn
COPY pom.xml /mvn/
COPY cli/pom.xml /mvn/cli/
COPY commons-lib/pom.xml /mvn/commons-lib/
COPY core/pom.xml /mvn/core/
COPY test-lib/pom.xml /mvn/test-lib/
WORKDIR /mvn
RUN mvn compile dependency:resolve dependency:resolve-plugins # --fail-never

FROM maven-git as mavenbuild
ARG ADDITIONAL_BUILD_ARG
ENV MAVEN_OPTS=-Dmaven.repo.local=/mvn
COPY . /mvn
COPY --from=mavencache /mvn/ /mvn/
WORKDIR /mvn
RUN set -x && mvn package -Djar ${ADDITIONAL_BUILD_ARG}
RUN rm -rf /mvn/cli/target/colander-cli-*-sources.jar && \
    rm -rf /mvn/cli/target/colander-cli-*-javadoc.jar
RUN mv /mvn/cli/target/colander-cli-*.jar /colander.jar

FROM debian:stretch-20190326-slim AS build-env

FROM oracle/graalvm-ce:19.0.0  AS native-image
COPY --from=mavenbuild /colander.jar /app/
WORKDIR /app

RUN gu install native-image
RUN native-image -H:+ReportExceptionStackTraces  \
  --static -H:Name=colander \
  # UnresolvedElementException: Discovered unresolved type during parsing: ch.qos.logback.classic.gaffer.GafferConfigurator
  --allow-incomplete-classpath \
  -jar colander.jar

FROM scratch
ARG VCS_REF
ARG SOURCE_REPOSITORY_URL
ARG GIT_TAG
ARG BUILD_DATE
# See https://github.com/opencontainers/image-spec/blob/master/annotations.md
LABEL org.opencontainers.image.created="${BUILD_DATE}" \
      org.opencontainers.image.authors="schnatterer" \
      org.opencontainers.image.url="https://hub.docker.com/r/schnatterer/colander/" \
      org.opencontainers.image.documentation="https://hub.docker.com/r/schnatterer/colander/" \
      org.opencontainers.image.source="${SOURCE_REPOSITORY_URL}" \
      org.opencontainers.image.version="${GIT_TAG}" \
      org.opencontainers.image.revision="${VCS_REF}" \
      org.opencontainers.image.vendor="schnatterer" \
      org.opencontainers.image.licenses="MIT" \
      org.opencontainers.image.title="colander" \
      org.opencontainers.image.description="colander - filtering your calendar"

COPY --from=build-env /lib/x86_64-linux-gnu/libz.so.1 /lib/x86_64-linux-gnu/libz.so.1
COPY --from=native-image /app/colander /colander
ENTRYPOINT ["/colander"]
# Build succeeds. However,
# - There's no logback.xml (not surprising without jar!)
# - The version cannot be loaded from MANIFEST.mf (not surprising without jar!)
# - --help does not show any options, probably because they are defined in annotations (using reflection)
#   https://github.com/oracle/graal/blob/master/substratevm/REFLECTION.md
