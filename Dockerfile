FROM ghcr.io/graalvm/native-image-community:21 AS build

# install dependencies
RUN microdnf install findutils

# create build dir and copy files
RUN mkdir /qw-push
ADD . /qw-push
WORKDIR /qw-push

# change gradlew script permissions
USER root
RUN chmod 755 ./gradlew

# build
RUN ./gradlew nativeCompile

FROM debian:12-slim

# copy binary
COPY --from=build /qw-push/build/native/nativeCompile/qw-push /bin/qw-push

ENTRYPOINT ["/bin/qw-push"]
