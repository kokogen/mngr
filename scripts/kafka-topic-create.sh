#!/bin/zsh

/bin/kafka-topics --bootstrap-server localhost:29092 --create --replication-factor 1 --partitions 1 --topic mssm-parts
/bin/kafka-topics --bootstrap-server localhost:29092 --create --replication-factor 1 --partitions 1 --topic mssm-tasks
/bin/kafka-topics --bootstrap-server localhost:9092 --describe --topic mssm-parts
/bin/kafka-topics --bootstrap-server localhost:9092 --describe --topic mssm-tasks