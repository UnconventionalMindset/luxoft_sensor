# Sensor Statistics

## Docker run

Run the following commands (only Windows Powershell was tested)

### Windows (Powershell):
```
$PATH='C:\Users\UnconventionalMindset'
$CONTAINER_PATH='/directory'
docker run -v ${PATH}:${CONTAINER_PATH} jaxergb/luxoft_sensor:1.0.0
```

### Windows (CMD):
```
set PATH='C:\Users\UnconventionalMindset'
set CONTAINER_PATH='/directory'
docker run -v %PATH%:%CONTAINER_PATH% jaxergb/luxoft_sensor:1.0.0
```
Linux:
```
PATH=/home/UnconventionalMindset/
CONTAINER_PATH=/directory
docker run -v ${PATH}:${CONTAINER_PATH} jaxergb/luxoft_sensor:1.0.0
```

## Local run

### Windows (Powershell)
```
$PATH='C:\Users\UnconventionalMindset'
sbt "run $PATH"
```
### Linux
```
PATH=/home/UnconventionalMindset/
sbt "run $PATH"
```

# BuildWith?
- Latest Scala version (3.3.1)
- Latest LTS OpenJDK (17.0.2)
- Latest Akka version (2.8.5)
- Latest SBT version (1.9.6)
- Latest Scalatest version (3.2.17)
