FROM phusion/baseimage:noble-1.0.0
ARG BUILDPLATFORM=linux/amd64
ARG ARCH=amd64
ARG JAVA_VERSION=17
ARG SDK_TOOLS=8512546_latest
ARG ANDROID_ROOT=/usr/local/lib/android
ARG USERNAME=ubuntu
ARG USER_UID=1000
ARG USER_GID=$USER_UID

# install the temurin jdk
RUN apt -qq update && DEBIAN_FRONTEND=noninteractive apt-get install -y \
    --no-install-recommends openjdk-$JAVA_VERSION-jdk \
    wget tree python3 python3-pip zip unzip git

RUN readlink -f $(which java)


## this will not work
# RUN set -eux; \
#     if [ "$BUILDPLATFORM" = "linux/arm64" ]; then arch=arm64; else arch=amd64; fi && \
#     export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-${arch} && \
#     echo ${JAVA_HOME} && \
#     ls -al ${JAVA_HOME}
## export env will not persist in final image

# /usr/lib/jvm/java-17-openjdk-amd64
ENV JAVA_HOME=/usr/lib/jvm/java-$JAVA_VERSION-openjdk-${ARCH}


RUN echo ${JAVA_HOME}


WORKDIR /tmp

# download android tools and use it to install the SDK
RUN mkdir -p ${ANDROID_ROOT}/sdk/cmdline-tools/latest

RUN echo ${ANDROID_ROOT}

RUN wget -O android-sdk.zip https://dl.google.com/android/repository/commandlinetools-linux-$SDK_TOOLS.zip && unzip android-sdk.zip && cp -r ./cmdline-tools/* ${ANDROID_ROOT}/sdk/cmdline-tools/latest

RUN echo ${ANDROID_ROOT}/sdk/cmdline-tools/latest/bin/sdkmanager

RUN /usr/local/lib/android/sdk/cmdline-tools/latest/bin/sdkmanager --licenses >/dev/null

RUN echo ${ANDROID_ROOT}



RUN echo "y" | ${ANDROID_ROOT}/sdk/cmdline-tools/latest/bin/sdkmanager --sdk_root=$ANDROID_ROOT/sdk/ \
  "platform-tools" \
  "platforms;android-34" \
  "platforms;android-33" \
  "platforms;android-32" \
  "platforms;android-31" \
  "build-tools;34.0.0" \
  "ndk;26.3.11579264" \
  "cmake;3.22.1" \
  "extras;android;m2repository" \
  "extras;google;m2repository"  1>/dev/null
  
RUN echo ${ANDROID_ROOT}


RUN if [ "$BUILDPLATFORM" = "linux/arm64" ]; then echo "ndk-bundle install fail on arm64 "; else \
    echo "y" | ${ANDROID_ROOT}/sdk/cmdline-tools/latest/bin/sdkmanager --sdk_root=$ANDROID_ROOT/sdk/ \
    "ndk-bundle"  \
    ; fi

WORKDIR /actions-runner
ENV PATH="${PATH}:/usr/local/lib/android/sdk/platform-tools/"

# Set env variable for SDK Root (https://developer.android.com/studio/command-line/variables)
# ANDROID_HOME is deprecated, but older versions of Gradle rely on it
ENV ANDROID_SDK_ROOT=$ANDROID_ROOT/sdk
ENV ANDROID_HOME=$ANDROID_ROOT/sdk
LABEL maintainer="ernstjason1@gmail.com"

# Create the user
RUN groupadd --gid $USER_GID $USERNAME \
    && useradd --uid $USER_UID --gid $USER_GID -m $USERNAME \
    #
    # [Optional] Add sudo support. Omit if you don't need to install software after connecting.
    && apt-get update \
    && apt-get install -y sudo \
    && echo $USERNAME ALL=\(root\) NOPASSWD:ALL > /etc/sudoers.d/$USERNAME \
    && chmod 0440 /etc/sudoers.d/$USERNAME




