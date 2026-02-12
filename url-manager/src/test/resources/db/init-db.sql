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
