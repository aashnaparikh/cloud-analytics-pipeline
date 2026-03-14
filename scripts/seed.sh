#!/usr/bin/env bash
# Seed the database with realistic demo data via the REST API
# Usage: ./scripts/seed.sh [BASE_URL] [EMAIL] [PASSWORD]
set -euo pipefail

BASE_URL="${1:-http://localhost:8080}"
EMAIL="${2:-admin@demo.com}"
PASSWORD="${3:-Demo1234!}"

echo "🔐 Authenticating..."
TOKEN=$(curl -s -X POST "$BASE_URL/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\"}" | jq -r .token)

if [[ -z "$TOKEN" || "$TOKEN" == "null" ]]; then
  echo "❌ Auth failed. Is the backend running at $BASE_URL?"
  exit 1
fi
echo "✅ Authenticated"

AUTH="Authorization: Bearer $TOKEN"
NOW=$(date -u +"%Y-%m-%dT%H:%M:%SZ")

echo "📨 Ingesting batch events..."
curl -s -X POST "$BASE_URL/api/v1/events/batch" \
  -H "Content-Type: application/json" \
  -H "$AUTH" \
  -d '[
    {"source":"web-app","eventType":"page_view","numericValue":1,"eventTimestamp":"'"$NOW"'"},
    {"source":"mobile-app","eventType":"click","numericValue":1,"eventTimestamp":"'"$NOW"'"},
    {"source":"api-gateway","eventType":"request","numericValue":120.5,"eventTimestamp":"'"$NOW"'"},
    {"source":"iot-sensor","eventType":"temperature","numericValue":23.7,"eventTimestamp":"'"$NOW"'"},
    {"source":"batch-job","eventType":"completed","numericValue":1,"eventTimestamp":"'"$NOW"'"}
  ]' | jq length
echo "✅ Events ingested"

echo "📊 Recording metrics..."
curl -s -X POST "$BASE_URL/api/v1/metrics/batch" \
  -H "Content-Type: application/json" \
  -H "$AUTH" \
  -d '[
    {"metricName":"cpu_usage","value":45.2,"unit":"%","recordedAt":"'"$NOW"'","aggregationType":"AVG"},
    {"metricName":"memory_usage","value":68.1,"unit":"%","recordedAt":"'"$NOW"'","aggregationType":"AVG"},
    {"metricName":"request_latency_ms","value":134.5,"unit":"ms","recordedAt":"'"$NOW"'","aggregationType":"AVG"},
    {"metricName":"error_rate","value":0.012,"unit":"%","recordedAt":"'"$NOW"'","aggregationType":"AVG"},
    {"metricName":"throughput_rps","value":285.0,"unit":"rps","recordedAt":"'"$NOW"'","aggregationType":"SUM"}
  ]' | jq length
echo "✅ Metrics recorded"

echo ""
echo "🎉 Seed complete! Open http://localhost or the Swagger UI at $BASE_URL/swagger-ui"
