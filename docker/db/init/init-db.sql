-- =============================================================================
-- PostgreSQL Multi-User Security Setup for URL Shortener
-- =============================================================================
-- This script is automatically run by Docker as POSTGRES_USER (postgres)
-- when the container is first initialized via docker-entrypoint-initdb.d
-- =============================================================================

-- =============================================================================
-- STEP 0: VERIFY RUNNING AS SUPERUSER
-- =============================================================================
DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1 FROM pg_roles
            WHERE rolname = current_user AND rolsuper = true
        ) THEN
            RAISE EXCEPTION 'This script must be run by a superuser. Current user: %', current_user;
        END IF;
        RAISE NOTICE 'Running as superuser: %', current_user;
    END $$;

-- =============================================================================
-- STEP 1: CREATE APPLICATION ROLES (NON-SUPERUSERS)
-- =============================================================================
DO $$
    BEGIN
        -- Create app_user for URL shortener app
        IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'app_user') THEN
            CREATE USER app_user WITH
                PASSWORD 'user'
                NOSUPERUSER
                NOCREATEDB
                NOCREATEROLE
                NOREPLICATION
                NOINHERIT
                CONNECTION LIMIT 50
                LOGIN;
            RAISE NOTICE 'Created app_user (non-superuser)';
        END IF;

        -- Create keycloak_user for Keycloak
        IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'keycloak_user') THEN
            CREATE USER keycloak_user WITH
                PASSWORD 'keycloak'
                NOSUPERUSER
                NOCREATEDB
                NOCREATEROLE
                NOREPLICATION
                NOINHERIT
                CONNECTION LIMIT 20
                LOGIN;
            RAISE NOTICE 'Created keycloak_user (non-superuser)';
        END IF;
    END $$;

-- =============================================================================
-- STEP 2: VERIFY NO ROLE INHERITANCE (SECURITY CHECK)
-- =============================================================================
DO $$
    BEGIN
        -- Ensure users are not members of each other's roles
        PERFORM 1 FROM pg_auth_members am
                           JOIN pg_roles m ON am.member = m.oid
                           JOIN pg_roles r ON am.roleid = r.oid
        WHERE m.rolname IN ('app_user', 'keycloak_user')
          AND r.rolname IN ('app_user', 'keycloak_user');

        IF FOUND THEN
            EXECUTE 'REVOKE app_user FROM keycloak_user';
            EXECUTE 'REVOKE keycloak_user FROM app_user';
            RAISE NOTICE 'Removed cross-role memberships';
        END IF;
    END $$;

-- =============================================================================
-- STEP 3: GRANT DATABASE CONNECTION
-- =============================================================================
GRANT CONNECT ON DATABASE shortener_db TO app_user;
GRANT CONNECT ON DATABASE shortener_db TO keycloak_user;

-- =============================================================================
-- STEP 4: CREATE SCHEMAS WITH OWNERS
-- =============================================================================
CREATE SCHEMA IF NOT EXISTS url_management  AUTHORIZATION app_user;
CREATE SCHEMA IF NOT EXISTS user_management AUTHORIZATION app_user;
CREATE SCHEMA IF NOT EXISTS url_analytics   AUTHORIZATION app_user;
CREATE SCHEMA IF NOT EXISTS keycloak        AUTHORIZATION keycloak_user;

-- =============================================================================
-- STEP 5: REVOKE ALL DEFAULT PERMISSIONS (DO THIS FIRST!)
-- =============================================================================

-- Lock down PUBLIC schema
REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE CREATE ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON ALL TABLES IN SCHEMA public FROM PUBLIC;
REVOKE ALL ON ALL SEQUENCES IN SCHEMA public FROM PUBLIC;
REVOKE ALL ON ALL FUNCTIONS IN SCHEMA public FROM PUBLIC;

-- Revoke PUBLIC access from all application schemas (CRITICAL!)
REVOKE ALL ON SCHEMA url_management FROM PUBLIC;
REVOKE ALL ON SCHEMA user_management FROM PUBLIC;
REVOKE ALL ON SCHEMA url_analytics FROM PUBLIC;
REVOKE ALL ON SCHEMA keycloak FROM PUBLIC;

-- Revoke cross-schema access at SCHEMA level
REVOKE ALL ON SCHEMA keycloak FROM app_user;
REVOKE ALL ON SCHEMA url_management FROM keycloak_user;
REVOKE ALL ON SCHEMA user_management FROM keycloak_user;
REVOKE ALL ON SCHEMA url_analytics FROM keycloak_user;

-- Revoke cross-schema access at OBJECT level (tables, sequences, functions)
-- This is CRITICAL because NULL relacl grants default PUBLIC access
REVOKE ALL ON ALL TABLES IN SCHEMA keycloak FROM PUBLIC;
REVOKE ALL ON ALL SEQUENCES IN SCHEMA keycloak FROM PUBLIC;
REVOKE ALL ON ALL FUNCTIONS IN SCHEMA keycloak FROM PUBLIC;
REVOKE ALL ON ALL TABLES IN SCHEMA keycloak FROM app_user;
REVOKE ALL ON ALL SEQUENCES IN SCHEMA keycloak FROM app_user;
REVOKE ALL ON ALL FUNCTIONS IN SCHEMA keycloak FROM app_user;

REVOKE ALL ON ALL TABLES IN SCHEMA url_management FROM PUBLIC;
REVOKE ALL ON ALL SEQUENCES IN SCHEMA url_management FROM PUBLIC;
REVOKE ALL ON ALL FUNCTIONS IN SCHEMA url_management FROM PUBLIC;
REVOKE ALL ON ALL TABLES IN SCHEMA url_management FROM keycloak_user;
REVOKE ALL ON ALL SEQUENCES IN SCHEMA url_management FROM keycloak_user;
REVOKE ALL ON ALL FUNCTIONS IN SCHEMA url_management FROM keycloak_user;

REVOKE ALL ON ALL TABLES IN SCHEMA user_management FROM PUBLIC;
REVOKE ALL ON ALL SEQUENCES IN SCHEMA user_management FROM PUBLIC;
REVOKE ALL ON ALL FUNCTIONS IN SCHEMA user_management FROM PUBLIC;
REVOKE ALL ON ALL TABLES IN SCHEMA user_management FROM keycloak_user;
REVOKE ALL ON ALL SEQUENCES IN SCHEMA user_management FROM keycloak_user;
REVOKE ALL ON ALL FUNCTIONS IN SCHEMA user_management FROM keycloak_user;

REVOKE ALL ON ALL TABLES IN SCHEMA url_analytics FROM PUBLIC;
REVOKE ALL ON ALL SEQUENCES IN SCHEMA url_analytics FROM PUBLIC;
REVOKE ALL ON ALL FUNCTIONS IN SCHEMA url_analytics FROM PUBLIC;
REVOKE ALL ON ALL TABLES IN SCHEMA url_analytics FROM keycloak_user;
REVOKE ALL ON ALL SEQUENCES IN SCHEMA url_analytics FROM keycloak_user;
REVOKE ALL ON ALL FUNCTIONS IN SCHEMA url_analytics FROM keycloak_user;

-- =============================================================================
-- STEP 6: GRANT MINIMAL REQUIRED PERMISSIONS
-- =============================================================================

-- Grant USAGE on public schema (needed for built-in functions)
GRANT USAGE ON SCHEMA public TO app_user, keycloak_user;

-- app_user: Full access to owned schemas
GRANT ALL PRIVILEGES ON SCHEMA url_management TO app_user;
GRANT ALL PRIVILEGES ON SCHEMA user_management TO app_user;
GRANT ALL PRIVILEGES ON SCHEMA url_analytics TO app_user;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA url_management TO app_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA url_management TO app_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA url_management TO app_user;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA user_management TO app_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA user_management TO app_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA user_management TO app_user;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA url_analytics TO app_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA url_analytics TO app_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA url_analytics TO app_user;

-- keycloak_user: Full access to owned schema
GRANT ALL PRIVILEGES ON SCHEMA keycloak TO keycloak_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA keycloak TO keycloak_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA keycloak TO keycloak_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA keycloak TO keycloak_user;

-- =============================================================================
-- STEP 7: SET SEARCH PATHS
-- =============================================================================
ALTER USER app_user SET search_path TO url_management, user_management, url_analytics, public;
ALTER USER keycloak_user SET search_path TO keycloak, public;

-- =============================================================================
-- STEP 8: DEFAULT PRIVILEGES FOR FUTURE OBJECTS
-- =============================================================================

-- Prevent default PUBLIC grants on future objects (CRITICAL!)
ALTER DEFAULT PRIVILEGES IN SCHEMA url_management REVOKE ALL ON TABLES FROM PUBLIC;
ALTER DEFAULT PRIVILEGES IN SCHEMA url_management REVOKE ALL ON SEQUENCES FROM PUBLIC;
ALTER DEFAULT PRIVILEGES IN SCHEMA url_management REVOKE ALL ON FUNCTIONS FROM PUBLIC;

ALTER DEFAULT PRIVILEGES IN SCHEMA user_management REVOKE ALL ON TABLES FROM PUBLIC;
ALTER DEFAULT PRIVILEGES IN SCHEMA user_management REVOKE ALL ON SEQUENCES FROM PUBLIC;
ALTER DEFAULT PRIVILEGES IN SCHEMA user_management REVOKE ALL ON FUNCTIONS FROM PUBLIC;

ALTER DEFAULT PRIVILEGES IN SCHEMA url_analytics REVOKE ALL ON TABLES FROM PUBLIC;
ALTER DEFAULT PRIVILEGES IN SCHEMA url_analytics REVOKE ALL ON SEQUENCES FROM PUBLIC;
ALTER DEFAULT PRIVILEGES IN SCHEMA url_analytics REVOKE ALL ON FUNCTIONS FROM PUBLIC;

ALTER DEFAULT PRIVILEGES IN SCHEMA keycloak REVOKE ALL ON TABLES FROM PUBLIC;
ALTER DEFAULT PRIVILEGES IN SCHEMA keycloak REVOKE ALL ON SEQUENCES FROM PUBLIC;
ALTER DEFAULT PRIVILEGES IN SCHEMA keycloak REVOKE ALL ON FUNCTIONS FROM PUBLIC;

-- When app_user creates objects in their schemas
ALTER DEFAULT PRIVILEGES FOR ROLE app_user IN SCHEMA url_management
    GRANT ALL ON TABLES TO app_user;
ALTER DEFAULT PRIVILEGES FOR ROLE app_user IN SCHEMA url_management
    GRANT ALL ON SEQUENCES TO app_user;
ALTER DEFAULT PRIVILEGES FOR ROLE app_user IN SCHEMA url_management
    GRANT ALL ON FUNCTIONS TO app_user;

ALTER DEFAULT PRIVILEGES FOR ROLE app_user IN SCHEMA user_management
    GRANT ALL ON TABLES TO app_user;
ALTER DEFAULT PRIVILEGES FOR ROLE app_user IN SCHEMA user_management
    GRANT ALL ON SEQUENCES TO app_user;
ALTER DEFAULT PRIVILEGES FOR ROLE app_user IN SCHEMA user_management
    GRANT ALL ON FUNCTIONS TO app_user;

ALTER DEFAULT PRIVILEGES FOR ROLE app_user IN SCHEMA url_analytics
    GRANT ALL ON TABLES TO app_user;
ALTER DEFAULT PRIVILEGES FOR ROLE app_user IN SCHEMA url_analytics
    GRANT ALL ON SEQUENCES TO app_user;
ALTER DEFAULT PRIVILEGES FOR ROLE app_user IN SCHEMA url_analytics
    GRANT ALL ON FUNCTIONS TO app_user;

-- When keycloak_user creates objects in their schema
ALTER DEFAULT PRIVILEGES FOR ROLE keycloak_user IN SCHEMA keycloak
    GRANT ALL ON TABLES TO keycloak_user;
ALTER DEFAULT PRIVILEGES FOR ROLE keycloak_user IN SCHEMA keycloak
    GRANT ALL ON SEQUENCES TO keycloak_user;
ALTER DEFAULT PRIVILEGES FOR ROLE keycloak_user IN SCHEMA keycloak
    GRANT ALL ON FUNCTIONS TO keycloak_user;

-- Cover case where superuser/other roles create objects in these schemas
ALTER DEFAULT PRIVILEGES IN SCHEMA url_management
    GRANT ALL ON TABLES TO app_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA url_management
    GRANT ALL ON SEQUENCES TO app_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA url_management
    GRANT ALL ON FUNCTIONS TO app_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA user_management
    GRANT ALL ON TABLES TO app_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA user_management
    GRANT ALL ON SEQUENCES TO app_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA user_management
    GRANT ALL ON FUNCTIONS TO app_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA url_analytics
    GRANT ALL ON TABLES TO app_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA url_analytics
    GRANT ALL ON SEQUENCES TO app_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA url_analytics
    GRANT ALL ON FUNCTIONS TO app_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA keycloak
    GRANT ALL ON TABLES TO keycloak_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA keycloak
    GRANT ALL ON SEQUENCES TO keycloak_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA keycloak
    GRANT ALL ON FUNCTIONS TO keycloak_user;

-- =============================================================================
-- STEP 9: VERIFICATION
-- =============================================================================
\echo ''
\echo '========================================='
\echo 'Security Setup Verification'
\echo '========================================='

\echo ''
\echo '=== Bootstrap Superuser ==='
SELECT current_user as bootstrap_superuser, rolsuper as is_superuser
FROM pg_roles WHERE rolname = current_user;

\echo ''
\echo '=== Application Role Configuration ==='
SELECT
    rolname,
    rolsuper as is_superuser,
    rolinherit as can_inherit,
    rolcreatedb as can_create_db,
    rolcanlogin as can_login,
    rolconnlimit as conn_limit
FROM pg_roles
WHERE rolname IN ('app_user', 'keycloak_user')
ORDER BY rolname;

\echo ''
\echo '=== Schema Ownership ==='
SELECT
    nspname as schema_name,
    nspowner::regrole as owner
FROM pg_namespace
WHERE nspname IN ('url_management', 'user_management', 'url_analytics', 'keycloak')
ORDER BY nspname;

\echo ''
\echo '=== app_user Schema Access ==='
SELECT
    nspname as schema_name,
    has_schema_privilege('app_user', nspname, 'USAGE') as has_usage,
    has_schema_privilege('app_user', nspname, 'CREATE') as has_create
FROM pg_namespace
WHERE nspname IN ('url_management', 'user_management', 'url_analytics', 'keycloak')
ORDER BY nspname;

\echo ''
\echo '=== keycloak_user Schema Access ==='
SELECT
    nspname as schema_name,
    has_schema_privilege('keycloak_user', nspname, 'USAGE') as has_usage,
    has_schema_privilege('keycloak_user', nspname, 'CREATE') as has_create
FROM pg_namespace
WHERE nspname IN ('url_management', 'user_management', 'url_analytics', 'keycloak')
ORDER BY nspname;

\echo ''
\echo '=== Role Memberships (should be empty) ==='
SELECT
    member.rolname as member_role,
    role.rolname as member_of_role
FROM pg_auth_members am
         JOIN pg_roles member ON am.member = member.oid
         JOIN pg_roles role ON am.roleid = role.oid
WHERE member.rolname IN ('app_user', 'keycloak_user')
   OR role.rolname IN ('app_user', 'keycloak_user');

\echo ''
\echo '========================================='
\echo 'Expected Results:'
\echo '  - bootstrap_superuser: postgres (is_superuser=t)'
\echo '  - is_superuser: FALSE for app_user and keycloak_user'
\echo '  - can_inherit: FALSE for both application users'
\echo '  - app_user: USAGE=t/CREATE=t for url_*, USAGE=f/CREATE=f for keycloak'
\echo '  - keycloak_user: USAGE=t/CREATE=t for keycloak, USAGE=f/CREATE=f for url_*'
\echo '  - Role Memberships: (empty - no rows)'
\echo '========================================='
\echo ''
\echo 'Connection strings for applications:'
\echo '  App:      postgresql://app_user:user@localhost:5432/shortener_db'
\echo '  Keycloak: postgresql://keycloak_user:keycloak@localhost:5432/shortener_db'
\echo ''
\echo 'SECURITY NOTE: Never use postgres superuser in application connections!'
\echo '========================================='