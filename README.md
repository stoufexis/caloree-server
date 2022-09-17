Server for [Caloree](https://github.com/StefanosTouf/caloree-cli)

## Setting up with Docker Compose

To get up and running quickly, specify the following environment variables in a file named `.env`

```bash
POSTGRES_PASSWORD # password used for the postgres user of the database
CALOREE_USER_PASSWORD # password used for the caloree user of the database user
CALOREE_DEFAULT_USERNAME # username of the caloree user that will be created on startup
CALOREE_DEFAULT_PASSWORD # password of the caloree user that will be created on startup
```

then, in the same directory, retrieve the `docker-compose` file, create the required volume, and start the service.

```bash
> wget https://raw.githubusercontent.com/StefanosTouf/caloree-server/master/docker/docker-compose.yml
> docker volume create db_data
> docker-compose up -d
```
