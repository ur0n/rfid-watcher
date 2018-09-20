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


