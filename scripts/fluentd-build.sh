#! /bin/bash

docker build -f fluentd/Dockerfile --tag aiph.work:5000/mpuarch/fluentd .

docker push aiph.work:5000/mpuarch/fluentd
