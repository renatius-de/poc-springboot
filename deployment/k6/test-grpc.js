import grpc from 'k6/net/grpc';
import { check } from 'k6';
import { SEARCH_PAGE_SIZE, SEED_STUDENTS, scenarios, thresholdsFor, summaryJson } from './common.js';

const GRPC_ADDR = __ENV.GRPC_ADDR || 'grpc-service.load-testing.svc.cluster.local:9090';
const PROTO_DIR = __ENV.PROTO_DIR || './proto';

const client = new grpc.Client();
client.load([PROTO_DIR], 'academic.proto');

export const options = {
  scenarios: scenarios('searchStudents'),
  thresholds: thresholdsFor('grpc_req_duration', { checks: ['rate>0.99'] }),
  summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(90)', 'p(95)', 'p(99)'],
};

let connected = false;

const SERVICE = 'de.renatius.poc.springboot.grpc.v1.StudentService';

export function setup() {
  client.connect(GRPC_ADDR, { plaintext: true });
  for (let i = 0; i < SEED_STUDENTS; i++) {
    const res = client.invoke(`${SERVICE}/CreateStudent`, {
      first_name: `Bench${i}`,
      last_name: `Student${i}`,
    });
    check(res, { 'seed created': (r) => r && r.status === grpc.StatusOK });
  }
  client.close();
}

export function searchStudents() {
  if (!connected) {
    client.connect(GRPC_ADDR, { plaintext: true });
    connected = true;
  }
  const res = client.invoke(`${SERVICE}/SearchStudents`, {
    first_name: 'Bench',
    page: { page: 0, size: SEARCH_PAGE_SIZE },
  });
  check(res, {
    'status is OK': (r) => r && r.status === grpc.StatusOK,
    'has students': (r) => r && r.message && r.message.students !== undefined,
  });
}

export function handleSummary(data) {
  const summary = summaryJson('grpc', data, 'grpc_req_duration', 'checks');
  return { stdout: `BENCHMARK_SUMMARY_JSON:${JSON.stringify(summary)}\n` };
}
