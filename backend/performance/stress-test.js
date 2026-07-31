import http from "k6/http";
  import { check, sleep } from "k6";

  const BASE_URL = "http://localhost:8080";

  export const options = {
      stages: [
          { duration: "30s", target: 10 },
          { duration: "1m", target: 25 },
          { duration: "1m", target: 50 },
          { duration: "1m", target: 75 },
          { duration: "30s", target: 0 },
      ],
      thresholds: {
          http_req_failed: ["rate<0.05"],
          http_req_duration: ["p(95)<1500"],
      },
  };

  export default function () {
      const loginRes = http.post(
          `${BASE_URL}/api/auth/login`,
          JSON.stringify({
              email: "test@example.com",
              password: "password123",
          }),
          {
              headers: {
                  "Content-Type": "application/json",
              },
          }
      );

      check(loginRes, {
          "login successful": (res) => res.status === 200,
      });

      const meRes = http.get(`${BASE_URL}/api/auth/me`);

      check(meRes, {
          "current user loaded": (res) => res.status === 200,
      });

      const therapistsRes = http.get(`${BASE_URL}/api/therapists`);

      check(therapistsRes, {
          "therapists loaded": (res) => res.status === 200,
      });

      const conversationsRes = http.get(`${BASE_URL}/api/message/conversations`);

      check(conversationsRes, {
          "conversations loaded": (res) => res.status === 200,
      });

      const appointmentsRes = http.get(`${BASE_URL}/api/appointments`);

      check(appointmentsRes, {
          "appointments loaded": (res) => res.status === 200,
      });

      sleep(1);
  }