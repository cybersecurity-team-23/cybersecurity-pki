# Cybersecurity PKI
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

## Contributors
- SV47/2021, Dragoslav Tamindžija
- SV50/2021, Darko Svilar
- SV67/2021, Teodor Đurić

## Prerequisites
1. Generate a jks file:
`keytool -genkey -alias keystore -keyalg RSA -keystore keystore.jks -keysize 4096 -storepass keystore123`
2. Manually add a mapping of the generated keystore file to it's password in
`src/main/resources/passwords-and-private-keys/keyStorePasswords.csv`. Paste the following: "keystore","keystore123"
3. Edit `src/main/resources/config.conf` to fit your information in the `[req_distinguished name]` section
4. Generate a root certificate in a directory of your choosing, using: `openssl req -x509 -nodes -days 7300 -newkey
rsa:4096 -sha256 -keyout rootCAKey.pem -out rootCACert.crt -config src/main/resources/config.conf -extensions my_ext`
5. Import the root certificate into the generated jks using: `keytool -trustcacerts -keystore
src/main/resources/keystore/keystore.jks -storepass keystore123 -importcert -alias
"<root_CA_issuer_email>|<root_CA_serial_number>" -file <relative_path_to_generated_root_CA>`