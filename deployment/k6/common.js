export const SEARCH_PAGE_SIZE = 20;
export const SEED_STUDENTS = 100;

// Identical load profile for REST and gRPC (open model, requests per second).
// Phase 1 low: 10-50 RPS, Phase 2 medium: 100-500 RPS, Phase 3 peak: 1000+ RPS.
export const stages = [
  { duration: '30s', target: 10 },
  { duration: '30s', target: 50 },
  { duration: '30s', target: 100 },
  { duration: '60s', target: 500 },
  { duration: '30s', target: 1000 },
  { duration: '60s', target: 1500 },
  { duration: '30s', target: 0 },
];

export function scenarios(exec) {
  return {
    load_profile: {
      executor: 'ramping-arrival-rate',
      startRate: 10,
      timeUnit: '1s',
      preAllocatedVUs: 200,
      maxVUs: 2000,
      stages,
      exec,
    },
  };
}

export function thresholdsFor(durationMetric, failedThreshold) {
  return {
    [durationMetric]: ['p(90)<500', 'p(95)<800', 'p(99)<1500'],
    ...failedThreshold,
    checks: ['rate>0.95'],
    iterations: ['rate>100'],
  };
}

export function summaryJson(protocol, data, durationMetric, failedMetric) {
  const m = data.metrics;
  const v = (name, key) => (m[name] && m[name].values[key] !== undefined ? m[name].values[key] : null);
  return {
    protocol,
    latency_ms: {
      p50: v(durationMetric, 'med'),
      p90: v(durationMetric, 'p(90)'),
      p95: v(durationMetric, 'p(95)'),
      p99: v(durationMetric, 'p(99)'),
      avg: v(durationMetric, 'avg'),
      max: v(durationMetric, 'max'),
    },
    throughput_rps: v('iterations', 'rate'),
    iterations: v('iterations', 'count'),
    error_rate: failedMetric === 'checks' ? 1 - (v('checks', 'rate') ?? 0) : v(failedMetric, 'rate'),
    dropped_iterations: v('dropped_iterations', 'count') ?? 0,
    max_vus: v('vus_max', 'max'),
    data_sent_bytes: v('data_sent', 'count'),
    data_received_bytes: v('data_received', 'count'),
  };
}
