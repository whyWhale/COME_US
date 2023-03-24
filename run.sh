#!/usr/bin/env sh

docker network rm come-us
echo "network create : come-us"
docker network create come-us

echo "volume remove not dangling! clean .ㅛ."
# shellcheck disable=SC2046
docker volume rm $(docker volume ls  -q --filter dangling=true)

echo "mysql redo log delete :)"
rm -r ./infra/mysql-replication/master/data

echo "docker already duplicate container  remove 'ㅅ' "
docker rm -f nginx mysql-master mysql-slave-1 mysql-phpmyadmin redis redis-commander redis-node1 redis-node2 redis-node3 redis-cluster-entry localstack come-us-application1 come-us-application2

chmod +x dockerize.sh

echo "application image build"
export APPLICATION_IMAGE=come-us-application;

./dockerize.sh

echo "docker up ready :)"
docker-compose up -d

echo "executing application and infra '-' "
