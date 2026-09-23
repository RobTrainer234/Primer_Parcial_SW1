ALTER TABLE proyectos ADD COLUMN IF NOT EXISTS owner_user_id VARCHAR(80);

UPDATE proyectos
SET owner_user_id = 'demo-admin'
WHERE owner_user_id IS NULL OR TRIM(owner_user_id) = '';

ALTER TABLE proyectos ALTER COLUMN owner_user_id SET NOT NULL;

CREATE INDEX IF NOT EXISTS ix_proyectos_owner_user_id ON proyectos(owner_user_id);
