CREATE TABLE events (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    source          VARCHAR(100)  NOT NULL,
    event_type      VARCHAR(100)  NOT NULL,
    tenant_id       VARCHAR(100)  NOT NULL,
    payload         TEXT,
    numeric_value   NUMERIC(20,6),
    string_value    VARCHAR(500),
    event_timestamp TIMESTAMPTZ   NOT NULL,
    ingested_at     TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    status          VARCHAR(20)   NOT NULL DEFAULT 'PROCESSED'
);

CREATE INDEX idx_events_timestamp   ON events(event_timestamp DESC);
CREATE INDEX idx_events_source_type ON events(source, event_type);
CREATE INDEX idx_events_tenant      ON events(tenant_id);
CREATE INDEX idx_events_tenant_ts   ON events(tenant_id, event_timestamp DESC);

-- Partition hint comment (for production, convert to partitioned table on event_timestamp)
COMMENT ON TABLE events IS 'Raw event ingestion table — consider range-partitioning on event_timestamp at scale';
