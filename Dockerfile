# use alpine version of PostgreSQL for a smaller image but with the same important functionality
FROM postgres:17-alpine

# copy the init script
# This script will be executed automatically when the container is started
COPY postgres-init.sql /docker-entrypoint-initdb.d/

# Expose the default PostgreSQL port
EXPOSE 5432