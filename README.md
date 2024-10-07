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
docker run -p 8080:8080 ogavrylenko/replicated-log:master
```
