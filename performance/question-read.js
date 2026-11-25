
import http from 'k6/http';
import { check } from 'k6';

const BASE_URL =
    __ENV.BASE_URL || 'http://gateway:8088';

const QUESTION_PATH =
    __ENV.QUESTION_PATH || '/api/questions/3';

const RATE = Number(__ENV.RATE || 10);

export const options = {
    scenarios: {
        question_read: {
            executor: 'constant-arrival-rate',

            rate: RATE,
            timeUnit: '1s',
            duration: __ENV.DURATION || '1m',

            preAllocatedVUs: 30,
            maxVUs: 100,
        },
    },

    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: [
            'p(95)<500',
            'p(99)<1000',
        ],
        checks: ['rate>0.99'],
    },
};

export default function () {

    const response = http.get(
        `${BASE_URL}${QUESTION_PATH}`,
        {
            tags: {
                endpoint: 'question-detail',
            },
            timeout: '10s',
        }
    );

    check(response, {
        'HTTP 200': (r) => r.status === 200,
        '响应体非空': (r) => r.body !== null
            && r.body.length > 0,
    });
}
