
FROM postgres:17

RUN apt-get update && \
    apt-get install -y curl postgresql-17-cron && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    echo "shared_preload_libraries = 'pg_cron'" >> /usr/share/postgresql/postgresql.conf.sample && \
    echo "cron.database_name = 'db1'" >> /usr/share/postgresql/postgresql.conf.sample