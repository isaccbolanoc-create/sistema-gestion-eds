#!/bin/bash
export JAVA_HOME=/home/issac/.jdks/openjdk-26.0.1-1
export PATH="/snap/intellij-idea/68/plugins/maven/lib/maven3/bin:$PATH"
mvn exec:java
