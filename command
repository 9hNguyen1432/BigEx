jhipster import-jdl ../jdl_config/jhipster-jdl.jdl
docker-compose -f src/main/docker/jhipster-registry.yml up -d
chmod +x mvnw
$ docker image rmi <image_id>
docker system prune