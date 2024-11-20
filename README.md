# How to Run with docker-compose
#### To build with maven
```maven
./mvnw clean install
```
#### Check images ogavrylenko/replicated-log-master:v1 and ogavrylenko/replicated-log-secondary:v1 don't exist in docker 
```Docker
docker images
```
delete images if exist
```Docker
docker rmi -f <img_id>
```
#### Run docker-compose
```Docker
docker-compose up
```
#### Curl commands to append messages and read list of messages from Master and secondaries
Master node runs on localhost:8080\
Secondary 1 node runs on localhost:8081\
Secondary 2 node runs on localhost:8082

```Shell
curl -H 'Content-Type: application/json' -X POST http://localhost:8080/append -d '{ "message":"msg1", "w":2}'

curl -X GET http://localhost:8080/list
curl -X GET http://localhost:8081/list
curl -X GET http://localhost:8082/list
```

# Alternatively Run with Docker

#### To build master Docker image

```Docker
docker build -f Dockerfile-master -t ogavrylenko/replicated-log-master:v2 .
```

#### To run master Docker container on port 8080

```Docker
docker run --rm -p 8080:8080 --net backend-net --name master ogavrylenko/replicated-log:master
```
#### To build secondary Docker image

```Docker
docker build -f Dockerfile-secondary -t ogavrylenko/replicated-log-secondary:v2 .
```
#### To run secondary Docker container on port 8081
```Docker
docker run --rm -p 8081:8081 --net backend-net --name secondary1 ogavrylenko/replicated-log-secondary:v2
```