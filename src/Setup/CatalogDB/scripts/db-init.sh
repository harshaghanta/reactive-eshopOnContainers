# Wait for sql server to startup
sleep 30s

# Run the setup script to create the DB and the schema in the DB
# Note: make sure that your password matches what is in the Dockerfile

echo 'entered db-init.sh'

/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P P@ssword1 -d master -i CatalogDB.sql