#### To build with maven
```maven
./mvnw package
```
#### To build master Docker image

```Docker
docker build -f Dockerfile-master -t ogavrylenko/replicated-log:master .
```

#### To run master Docker container on port 8080

```Docker
docker run --rm -p 8080:8080 --net log-net --name master ogavrylenko/replicated-log:master
```
#### To build secondary Docker image

```Docker
docker build -f Dockerfile-secondary -t ogavrylenko/replicated-log:secondary .
```
#### To run secondary Docker container on port 8081
```Docker
docker run --rm -p 8081:8081 --name sec1 ogavrylenko/replicated-log:secondary
```