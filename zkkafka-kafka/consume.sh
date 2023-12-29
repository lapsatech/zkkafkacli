#!/bin/bash
cwd=$(cd $(dirname $0); pwd)
${cwd}/execjava.sh kafka.KafkaConsume $@
