#!/bin/bash

class=$1
shift

old="$IFS"
IFS=','
args="$*"
IFS=$old

echo mvn compile exec:java -Dexec.mainClass=${class} -Dexec.arguments=${args}
mvn compile exec:java -Dexec.mainClass=${class} -Dexec.arguments=${args}
