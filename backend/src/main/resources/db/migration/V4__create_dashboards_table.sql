CREATE TABLE dashboards (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name          VARCHAR(200) NOT NULL,
    description   TEXT,
    tenant_id     VARCHAR(100) NOT NULL,
    created_by    VARCHAR(100) NOT NULL,
    configuration TEXT,        -- JSON layout/widget config
    is_public     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_dashboards_tenant ON dashboards(tenant_id);

-- Seed a default demo dashboard
INSERT INTO dashboards (name, description, tenant_id, created_by, is_public)
VALUES ('Overview', 'Default system overview dashboard', 'demo', 'system', TRUE);
