# create manager
docker-machine create -d virtualbox st-manager

# create workers
for i in (seq 3)
  docker-machine create -d virtualbox st-worker$i
end

# swarm init
docker-machine ssh st-manager \
  docker swarm init \
    --advertise-addr=(docker-machine ip st-manager) \
    --listen-addr=0.0.0.0:2377

# set token for environment variables
eval (docker-machine env st-manager)

set -x MANAGER_TOKEN (docker swarm join-token -q manager)
set -x WORKER_TOKEN (docker swarm join-token -q worker)

# swarm join
for i in (seq 3)
  docker-machine ssh st-worker$i \
  docker swarm join \
  --advertise-addr=(docker-machine ip st-worker$i) \
  --token=$WORKER_TOKEN \
  (docker-machine ip st-manager):2377
end
