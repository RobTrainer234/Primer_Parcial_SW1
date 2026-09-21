#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"

if ! command -v curl >/dev/null 2>&1; then
  echo "curl no esta instalado" >&2
  exit 1
fi

if ! command -v python >/dev/null 2>&1; then
  echo "python no esta instalado" >&2
  exit 1
fi

json_post() {
  local ruta="$1"
  local cuerpo="$2"
  curl -sS --fail -H "Content-Type: application/json" -X POST "$BASE_URL$ruta" -d "$cuerpo"
}

json_get() {
  local ruta="$1"
  curl -sS --fail "$BASE_URL$ruta"
}

extraer_id() {
  python -c 'import json,sys; print(json.load(sys.stdin)["id"])'
}

nombre_proyecto="Clinica Demo $(date +%s)"

echo "1) Crear proyecto"
proyecto=$(json_post "/proyectos" "{\"nombre\":\"$nombre_proyecto\",\"descripcion\":\"Proyecto de prueba automatizada\"}")
proyecto_id=$(printf '%s' "$proyecto" | extraer_id)
echo "   proyecto_id=$proyecto_id"

echo "2) Crear modelo"
modelo=$(json_post "/proyectos/$proyecto_id/modelos" '{"nombre":"Clinica"}')
modelo_id=$(printf '%s' "$modelo" | extraer_id)
echo "   modelo_id=$modelo_id"

echo "3) Crear entidad Paciente"
entidad=$(json_post "/modelos/$modelo_id/entidades" '{"nombre":"Paciente","posicionX":100,"posicionY":100}')
entidad_id=$(printf '%s' "$entidad" | extraer_id)
echo "   entidad_id=$entidad_id"

echo "4) Crear atributos"
json_post "/entidades/$entidad_id/atributos" '{"nombre":"id","tipoDato":"ENTERO_LARGO","clavePrimaria":true,"obligatorio":true,"valorUnico":true}' >/dev/null
json_post "/entidades/$entidad_id/atributos" '{"nombre":"nombre","tipoDato":"TEXTO","clavePrimaria":false,"obligatorio":true,"valorUnico":false}' >/dev/null

echo "5) Validar modelo"
validacion=$(json_post "/modelos/$modelo_id/validacion" '{}')
printf '%s\n' "$validacion"

echo "6) Generar backend"
generacion=$(json_post "/modelos/$modelo_id/generaciones" '{}')
generacion_id=$(printf '%s' "$generacion" | extraer_id)
printf '%s\n' "$generacion"

echo "7) Descargar artefacto"
mkdir -p generados
curl -sS --fail -L "$BASE_URL/generaciones/$generacion_id/artefacto" -o "generados/backend-generado-$generacion_id.zip"
echo "   generado: generados/backend-generado-$generacion_id.zip"

echo "Flujo completado"
