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
FROM maven:3.6.0-jdk-8-alpine as maven

FROM maven as mavencache
ENV MAVEN_OPTS=-Dmaven.repo.local=/mvn
COPY pom.xml /mvn/
COPY cli/pom.xml /mvn/cli/
COPY commons-lib/pom.xml /mvn/commons-lib/
COPY core/pom.xml /mvn/core/
COPY test-lib/pom.xml /mvn/test-lib/
WORKDIR /mvn
RUN echo a
RUN mvn dependency:resolve dependency:resolve-plugins --fail-never

FROM maven as mavenbuild
ENV MAVEN_OPTS=-Dmaven.repo.local=/mvn
COPY . /mvn
COPY --from=mavencache /mvn/ /mvn/
WORKDIR /mvn
RUN set -x && mvn package -Djar
RUN mv /mvn/cli/target/colander-cli-*.jar /colander.jar

FROM gcr.io/distroless/java:8
ARG VCS_REF
ARG SOURCE_REPOSITORY_URL
ARG GIT_TAG
ARG BUILD_DATE
# See https://github.com/opencontainers/image-spec/blob/master/annotations.md
LABEL org.opencontainers.image.created="${BUILD_DATE}" \
      org.opencontainers.image.authors="schnatterer" \
      org.opencontainers.image.url="${SOURCE_REPOSITORY_URL}" \
      org.opencontainers.image.documentation="${SOURCE_REPOSITORY_URL}" \
      org.opencontainers.image.source="${SOURCE_REPOSITORY_URL}" \
      org.opencontainers.image.version="${GIT_TAG}" \
      org.opencontainers.image.revision="${VCS_REF}" \
      org.opencontainers.image.vendor="schnatterer" \
      org.opencontainers.image.licenses="MIT" \
      org.opencontainers.image.title="colander" \
      org.opencontainers.image.description="colander - filtering your calendar"

COPY --from=mavenbuild /colander.jar /app/colander.jar
ENTRYPOINT ["java", "-jar", "/app/colander.jar"]
