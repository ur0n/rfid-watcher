eval (docker-machine env st-manager)

# rm stack
docker stack rm st

# leave and delete swarm node
docker node ls -f role=worker |\
grep Ready |\
awk '{print $2}' |\
xargs -I{machine-name} docker-machine ssh machine-name \
docker swarm leave \

docker node ls -f role=worker |\
grep Ready |\
awk '{print $2}' |\
xargs -I{machine-name} docker-machine ssh machine-name \
docker node rm machine-name

# stop and delete worker machine and delete
docker-machine ls -q | grep st-worker | xargs -I{machine-name} docker-machine stop machine-name
docker-machine ls -q | grep st-worker | xargs -I{machine-name} docker-machine rm machine-name -y

# stop and delete manager
docker-machine stop st-manager
docker-machine rm st-manager -y
