#!/bin/bash
/bin/kafka-console-consumer --topic kaspar --from-beginning --bootstrap-server localhost:9092 --property print.key=true -- property print.timestamp=true
