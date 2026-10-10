import http from 'k6/http';
import { check } from 'k6';
import { SEARCH_PAGE_SIZE, SEED_STUDENTS, scenarios, thresholdsFor, summaryJson } from './common.js';

const BASE_URL = __ENV.REST_URL || 'http://rest-service.load-testing.svc.cluster.local:8080';

export const options = {
  scenarios: scenarios('searchStudents'),
  thresholds: thresholdsFor('http_req_duration', { http_req_failed: ['rate<0.01'] }),
  summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(90)', 'p(95)', 'p(99)'],
};

export function setup() {
  const params = { headers: { 'Content-Type': 'application/json' } };
  for (let i = 0; i < SEED_STUDENTS; i++) {
    const res = http.post(
      `${BASE_URL}/api/students`,
      JSON.stringify({ firstName: `Bench${i}`, lastName: `Student${i}` }),
      params,
    );
    check(res, { 'seed created': (r) => r.status === 201 });
  }
}

export function searchStudents() {
  const res = http.get(
    `${BASE_URL}/api/students/search?first_name=Bench&page=0&size=${SEARCH_PAGE_SIZE}`,
    { tags: { name: 'SearchStudents' } },
  );
  check(res, {
    'status is 200': (r) => r.status === 200,
    'has content': (r) => r.json('content') !== undefined,
  });
}

export function handleSummary(data) {
  const summary = summaryJson('rest', data, 'http_req_duration', 'http_req_failed');
  return { stdout: `BENCHMARK_SUMMARY_JSON:${JSON.stringify(summary)}\n` };
}
