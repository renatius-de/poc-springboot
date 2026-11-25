import http from 'k6/http';
import { check } from 'k6';
import { SEARCH_PAGE_SIZE, SEED_STUDENTS, scenarios, thresholdsFor, summaryJson } from './common.js';

const BASE_URL = __ENV.GRAPHQL_URL || 'http://graphql-service.load-testing.svc.cluster.local:8084/graphql';
const API_KEY = __ENV.APP_SECURITY_API_KEY || 'benchmark-api-key';

const headers = {
  'Content-Type': 'application/json',
  'X-API-Key': API_KEY,
};

export const options = {
  scenarios: scenarios('searchStudents'),
  thresholds: thresholdsFor('http_req_duration', { http_req_failed: ['rate<0.01'] }),
  summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(90)', 'p(95)', 'p(99)'],
};

export function setup() {
  const mutation =
    'mutation CreateStudent($input: StudentInput!) { createStudent(input: $input) { id } }';
  for (let i = 0; i < SEED_STUDENTS; i++) {
    const res = http.post(
      BASE_URL,
      JSON.stringify({
        query: mutation,
        variables: { input: { firstName: `Bench${i}`, lastName: `Student${i}` } },
      }),
      { headers },
    );
    check(res, { 'seed created': (r) => r.status === 200 && r.json('data.createStudent.id') !== undefined });
  }
}

export function searchStudents() {
  const query =
    'query SearchStudents($firstName: String, $page: Int!, $size: Int!) { students(firstName: $firstName, page: $page, size: $size) { content { id firstName lastName } totalElements } }';
  const res = http.post(
    BASE_URL,
    JSON.stringify({
      query,
      variables: { firstName: 'Bench', page: 0, size: SEARCH_PAGE_SIZE },
    }),
    { headers, tags: { name: 'SearchStudents' } },
  );
  check(res, {
    'status is 200': (r) => r.status === 200,
    'has content': (r) => r.json('data.students.content') !== undefined,
  });
}

export function handleSummary(data) {
  const summary = summaryJson('graphql', data, 'http_req_duration', 'http_req_failed');
  return { stdout: `BENCHMARK_SUMMARY_JSON:${JSON.stringify(summary)}\n` };
}
