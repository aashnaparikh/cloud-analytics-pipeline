-- Note: demo users are created by DataSeeder.java at startup (passwords encoded at runtime)

-- Seed sample events
INSERT INTO events (source, event_type, tenant_id, numeric_value, event_timestamp)
SELECT
    (ARRAY['web-app','mobile-app','api-gateway','batch-job','iot-sensor'])[floor(random()*5+1)],
    (ARRAY['page_view','click','purchase','login','error','timeout','upload'])[floor(random()*7+1)],
    'demo',
    round((random() * 1000)::numeric, 2),
    NOW() - (random() * INTERVAL '7 days')
FROM generate_series(1, 500);

-- Seed sample metrics
INSERT INTO metrics (metric_name, tenant_id, value, unit, recorded_at, aggregation_type)
SELECT
    (ARRAY['cpu_usage','memory_usage','request_latency_ms','error_rate','throughput_rps','active_users'])[floor(random()*6+1)],
    'demo',
    round((random() * 100)::numeric, 4),
    (ARRAY['%','MB','ms','%','rps','count'])[floor(random()*6+1)],
    NOW() - (random() * INTERVAL '7 days'),
    'AVG'
FROM generate_series(1, 1000);
