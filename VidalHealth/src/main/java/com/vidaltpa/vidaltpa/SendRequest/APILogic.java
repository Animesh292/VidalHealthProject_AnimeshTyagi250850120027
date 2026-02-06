package com.vidaltpa.vidaltpa.SendRequest;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class APILogic {

    private static final String GENERATE_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

    private final RestTemplate restTemplate = new RestTemplate();

    public void runTask() {

        Map<String, String> requestBody = Map.of("name", "John Doe", "regNo", "REG12347", "email", "john@example.com");

        ResponseEntity<Map> response = restTemplate.postForEntity(GENERATE_URL, requestBody, Map.class);

        if (response.getBody() == null) {
            throw new RuntimeException("No response body from generateWebhook");
        }

        String webhook = (String) response.getBody().get("webhook");
        String token = (String) response.getBody().get("accessToken");

        String finalQuery = "SELECT d.department_name AS DEPARTMENT_NAME, emp_total.total_salary AS SALARY, CONCAT(e.first_name, ' ', e.last_name) AS EMPLOYEE_NAME, EXTRACT(YEAR FROM AGE(CURRENT_DATE, e.dob)) AS AGE FROM department d JOIN employee e ON e.department = d.department_id JOIN (SELECT p.emp_id, SUM(p.amount) AS total_salary FROM payments p WHERE EXTRACT(DAY FROM p.payment_time) <> 1 GROUP BY p.emp_id) emp_total ON emp_total.emp_id = e.emp_id WHERE emp_total.total_salary = (SELECT MAX(emp_inner.total_salary)  FROM (SELECT p2.emp_id,SUM(p2.amount) AS total_salary FROM payments p2 WHERE EXTRACT(DAY FROM p2.payment_time) <> 1 GROUP BY p2.emp_id) emp_inner JOIN employee e2 ON e2.emp_id = emp_inner.emp_id WHERE e2.department = d.department_id);";

        submit(webhook, token, finalQuery);
    }

    private void submit(String webhook, String token, String sql) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);

        Map<String, String> body = Map.of("finalQuery", sql);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(webhook, entity, String.class);
        System.out.println("Submission Response: " + response.getBody());
    }
}
