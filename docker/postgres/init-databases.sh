#!/bin/bash
set -e

# Script de inicializacion para PostgreSQL
# Crea multiples bases de datos para los diferentes microservicios
# El usuario principal se crea automaticamente via POSTGRES_USER

echo "Iniciando creacion de bases de datos..."

# Crear las bases de datos
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    -- Base de datos para usuarios
    CREATE DATABASE usuarios_db;

    -- Base de datos para catalogo
    CREATE DATABASE catalogo_db;

    -- Base de datos para biblioteca
    CREATE DATABASE biblioteca_db;
EOSQL

# Otorgar permisos en el schema public de cada base de datos
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "usuarios_db" <<-EOSQL
    GRANT ALL ON SCHEMA public TO $POSTGRES_USER;
    GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO $POSTGRES_USER;
    ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO $POSTGRES_USER;
EOSQL

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "catalogo_db" <<-EOSQL
    GRANT ALL ON SCHEMA public TO $POSTGRES_USER;
    GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO $POSTGRES_USER;
    ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO $POSTGRES_USER;
EOSQL

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "biblioteca_db" <<-EOSQL
    GRANT ALL ON SCHEMA public TO $POSTGRES_USER;
    GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO $POSTGRES_USER;
    ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO $POSTGRES_USER;
EOSQL

echo "=========================================="
echo "Bases de datos creadas exitosamente:"
echo "  - usuarios_db"
echo "  - catalogo_db"
echo "  - biblioteca_db"
echo "Usuario: $POSTGRES_USER"
echo "=========================================="
