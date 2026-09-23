ALTER TABLE relaciones_modelo ADD COLUMN tipo VARCHAR(40) DEFAULT 'ASOCIACION';

UPDATE relaciones_modelo
SET tipo = 'ASOCIACION'
WHERE tipo IS NULL;

ALTER TABLE relaciones_modelo ALTER COLUMN tipo SET NOT NULL;
