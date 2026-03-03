#!/bin/bash
# Script para inicializar el índice de OpenSearch para el microservicio busquedas.
# Ejecutar una sola vez antes de arrancar el servicio.
#
# Uso:
#   bash init-opensearch.sh
#   bash init-opensearch.sh http://localhost:9200   # URL personalizada

OPENSEARCH_URL="${1:-http://localhost:9200}"
INDEX="games-v1"
READ_ALIAS="games-read"
WRITE_ALIAS="games-write"

echo "⏳ Esperando a que OpenSearch esté disponible en $OPENSEARCH_URL..."
until curl -s -o /dev/null "$OPENSEARCH_URL/_cluster/health"; do
  sleep 2
done
echo "✅ OpenSearch disponible."

echo "🔧 Creando índice $INDEX con mapeo..."
curl -s -X PUT "$OPENSEARCH_URL/$INDEX" \
  -H "Content-Type: application/json" \
  -d '{
  "mappings": {
    "properties": {
      "gameId":           { "type": "long" },
      "title":            { "type": "text" },
      "alternativeNames": { "type": "text" },
      "nameSuggest":      { "type": "completion" }
    }
  }
}' | python3 -m json.tool 2>/dev/null || true

echo ""
echo "🔗 Creando alias $READ_ALIAS y $WRITE_ALIAS..."
curl -s -X POST "$OPENSEARCH_URL/_aliases" \
  -H "Content-Type: application/json" \
  -d "{
  \"actions\": [
    { \"add\": { \"index\": \"$INDEX\", \"alias\": \"$READ_ALIAS\"  } },
    { \"add\": { \"index\": \"$INDEX\", \"alias\": \"$WRITE_ALIAS\" } }
  ]
}" | python3 -m json.tool 2>/dev/null || true

echo ""
echo "✅ Índice $INDEX listo con aliases $READ_ALIAS / $WRITE_ALIAS."

