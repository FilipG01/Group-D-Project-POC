import http from "k6/http";
  import { check, sleep } from "k6";

  const BASE_URL = "http://localhost:8080";

  export const options = {
      vus: 1,
      iterations: 1,
      thresholds: {
          http_req_failed: ["rate<0.01"],
          http_req_duration: ["p(95)<500"],
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

      const logoutRes = http.post(`${BASE_URL}/api/auth/logout`);

      check(logoutRes, {
          "logout successful": (res) =>
              res.status === 200 ||
              res.status === 204,
      });

      sleep(1);
  }