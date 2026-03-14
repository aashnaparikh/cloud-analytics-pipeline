CREATE TABLE metrics (
    id               UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    metric_name      VARCHAR(200)  NOT NULL,
    tenant_id        VARCHAR(100)  NOT NULL,
    value            NUMERIC(20,6) NOT NULL,
    unit             VARCHAR(50),
    recorded_at      TIMESTAMPTZ   NOT NULL,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    aggregation_type VARCHAR(50),
    tags             TEXT          -- JSON key-value pairs
);

CREATE INDEX idx_metrics_name_time ON metrics(metric_name, recorded_at DESC);
CREATE INDEX idx_metrics_tenant    ON metrics(tenant_id);
CREATE INDEX idx_metrics_tenant_name_time ON metrics(tenant_id, metric_name, recorded_at DESC);
