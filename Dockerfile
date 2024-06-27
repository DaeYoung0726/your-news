FROM ubuntu:latest
LABEL authors="daeyoung"

ENTRYPOINT ["top", "-b"]