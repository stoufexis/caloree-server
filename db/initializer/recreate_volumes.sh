#!/bin/bash

docker volume rm db_data &&
  docker volume rm initializer_data &&
  docker volume create db_data &&
  docker volume create initializer_data
