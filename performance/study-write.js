
import http from 'k6/http';
import { check } from 'k6';

const BASE_URL =
    __ENV.BASE_URL || 'http://gateway:8088';

const STUDY_PATH =
    __ENV.STUDY_PATH || '/api/study/views/3';

const TOKEN = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzIiwicm9sZXMiOlsiVVNFUiJdLCJpc3MiOiJwYXNzamF2YS1sZWFybmluZyIsImV4cCI6MTc5NTU5NzA5MiwiaWF0IjoxNzk1NTg5ODkyLCJ1c2VybmFtZSI6InRlc3QifQ.r0byWu5TpSs90ENheLYSzXy4EUqWtKhi56qEDJLfq4A';

if (!TOKEN) {
    throw new Error(
        '必须提供 PASSJAVA_TEST_TOKEN'
    );
}

export const options = {
    scenarios: {
        study_write: {
            executor: 'constant-arrival-rate',
            rate: Number(__ENV.RATE || 5),
            timeUnit: '1s',
            duration: __ENV.DURATION || '1m',
            preAllocatedVUs: 20,
            maxVUs: 80,
        },
    },

    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: [
            'p(95)<800',
            'p(99)<1500',
        ],
        checks: ['rate>0.99'],
    },
};

export default function () {

    const response = http.post(
        `${BASE_URL}${STUDY_PATH}`,
        null,
        {
            headers: {
                Authorization: `Bearer ${TOKEN}`,
            },
            tags: {
                endpoint: 'study-view',
            },
            timeout: '15s',
        }
    );

    check(response, {
        '记录学习成功': (r) => r.status === 200,
    });
}
