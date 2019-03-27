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

FROM maven as mavenbuild
COPY . /build
WORKDIR /build
RUN set -x && mvn package -Djar

FROM debian:stretch-20190326-slim AS build-env

FROM oracle/graalvm-ce:1.0.0-rc14 AS native-image
COPY --from=mavenbuild /build/cli/target/colander-cli-*.jar /app/
WORKDIR /app
RUN native-image -H:+ReportExceptionStackTraces  \
  --static -H:Name=colander \
  --delay-class-initialization-to-runtime=ch.qos.logback.classic.Logger \
  -jar $(ls colander-cli-*.jar)

FROM gcr.io/distroless/base
COPY --from=native-image /app/colander /colander
COPY --from=build-env /lib/x86_64-linux-gnu/libz.so.1 /lib/x86_64-linux-gnu/libz.so.1
CMD ["/colander"]
