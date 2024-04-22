## dummy data and schema in db-stuff/init/

## copy .env.example to .env and adjust vals

## pgAdmin running on http://localhost:5050
- log in with PGADMIN_DEFAULT_EMAIL and PGADMIN_DEFAULT_PASSWORD
- access db with POSTGRES_USER and POSTGRES_PASSWORD

## start
`docker compose up`

## (re)build and run
`docker compose up --build`

## teardown
`docker compose down`

## teardown including volumes (db data)
`docker compose down -v`

## run in background
`docker compose up -d`

## full rebuild and run in background
`docker compose down -v && docker compose up --build -d`
