// Script de inicializacion para MongoDB
// Crea bases de datos y usuarios para los microservicios

print('===========================================');
print('Iniciando configuracion de MongoDB...');
print('===========================================');

// Cambiar a la base de datos admin para autenticacion
db = db.getSiblingDB('admin');

// El usuario root ya esta creado via MONGO_INITDB_ROOT_USERNAME/PASSWORD
// Solo necesitamos crear las bases de datos y otorgar permisos

// ============================================
// CATALOGO SERVICE - Base de datos para detalles de juegos
// ============================================
db = db.getSiblingDB('catalogo_db');

print('Creando base de datos: catalogo_db');

// Crear una coleccion dummy para que la BD se cree fisicamente
db.createCollection('_init');
db._init.insertOne({initialized: true, date: new Date()});

print('Base de datos catalogo_db creada exitosamente');

// ============================================
// PUBLICACIONES SERVICE (futuro) - Base de datos para posts/comentarios
// ============================================
db = db.getSiblingDB('publicaciones_db');

print('Creando base de datos: publicaciones_db');

db.createCollection('_init');
db._init.insertOne({initialized: true, date: new Date()});

print('Base de datos publicaciones_db creada exitosamente');

// ============================================
// SOCIAL SERVICE (futuro) - Base de datos para grafos sociales
// ============================================
// Nota: Neo4j se usara para el grafo social, MongoDB puede usarse para cache

print('===========================================');
print('Configuracion de MongoDB completada');
print('Bases de datos creadas:');
print('  - catalogo_db');
print('  - publicaciones_db');
print('Usuario root configurado via variables de entorno');
print('===========================================');

