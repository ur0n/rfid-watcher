# RFID Watcher

Start server

```
$ sudo su -
$ cd /home/rfid/workspace/rfid-watcher
$ docker-compose up
```

Connect to redis

```
$ docker-compose exec redis /bin/bash
```

Show redis log

```
$ docker-compose logs redis
```

Stop server

```
$ cd /home/rfid/workspace/rfid-watcher
$ docker-compose down
```

today's data

``````
/home/rfid/workspace/rfid-watcher/batch/files/*

``````


=======
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
