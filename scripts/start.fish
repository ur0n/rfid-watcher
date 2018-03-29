# deploy as docker stack specified in docker-compose file using -c option
# docker stck deploy -c {file_name} {stack name}
eval (docker-machine env st-manager)
docker stack deploy -c docker-compose.yml st
