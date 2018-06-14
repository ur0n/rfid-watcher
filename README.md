# rfid-watcher

## Requirement
Download OctaneSDK and put OctaneSDK/lib in this root directory

## Build
```
$ bash scripts/gradle-build.sh
```

## Run
```
$ docker-compose up
```

## Debug
### redis
```
$ docker-compose exec redis /bin/bash

redis$ redis-cli
```

## Data

### today's data

```
/path/to/dir/outputs/*
```
### 1hour data

```
/path/to/dir/batch/files/*
```
