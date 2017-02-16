#!/bin/bash

set -o xtrace
mvn package -DskipTests -f solversvc/star \
&& mvn package -DskipTests -f solversvc/client \
&& cp solversvc/star/target/star*.jar solversvc/apps/star.jar \
&& cp solversvc/client/target/client*.jar solversvc/apps/client.jar
set +o xtrace
