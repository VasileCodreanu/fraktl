-- create a cron that would delete records based on expiration_at < now()
-- TODO find out a way to do this job as restricted user

CREATE EXTENSION IF NOT EXISTS pg_cron;

SELECT cron.schedule(
               'delete_expired_urls',
               '0 0 * * *',
               $$DELETE FROM url_mappings WHERE url_mappings.expiration_at < now();$$
);

-- SELECT * FROM cron.job;

-- To view setting configurations, run:
-- SELECT * FROM pg_settings WHERE name LIKE 'cron.%';
