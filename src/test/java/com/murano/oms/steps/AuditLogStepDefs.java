package com.murano.oms.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import flextrade.flexvision.fx.audit.pojo.AuditLog;
import flextrade.flexvision.fx.audit.service.AuditLogService;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@WebIntegrationTest
public class AuditLogStepDefs extends AbstractSteps {

    private AuditLog auditLog;

    private ResponseEntity<String> httpPostResponse;

    private String httpGetResponse;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private ObjectMapper objectMapper;

    private RestTemplate restTemplate = new TestRestTemplate();


    @Given("^maxx user is \"([^\"]*)\", operation is \"([^\"]*)\", audit time is at \"([^\"]*)\", and remark is \"([^\"]*)\"$")
    public void maxx_user_is_operation_is_audit_time_is_at_and_remark_is(String maxxUser, String operation, String auditDate, String remarks) throws Throwable {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        auditLog = new AuditLog();
        auditLog.setMaxxUser(maxxUser);
        auditLog.setOperation(operation);
        auditLog.setRemarks(remarks);
        auditLog.setAuditDate(dateFormatter.parse(auditDate));
    }

    @Then("^the audit log server response should be maxx user is \"([^\"]*)\", operation is \"([^\"]*)\", audit time is at \"([^\"]*)\", and remark is \"([^\"]*)\"$")
    public void the_audit_log_server_response_should_be_maxx_user_is_operation_is_audit_time_is_at_and_remark_is(String expectedMaxxUser, String ExpectedOperation, String expectedAuditDate, String expectedRemark) throws Throwable {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXX");
        List<AuditLog> auditLogsFromServerResponse = objectMapper.readValue(httpGetResponse, new TypeReference<List<AuditLog>>() {
        });

        assertEquals(1, auditLogsFromServerResponse.size());
        AuditLog auditLogFromServerResponse = auditLogsFromServerResponse.get(0);
        assertEquals(expectedMaxxUser, auditLogFromServerResponse.getMaxxUser());
        assertEquals(ExpectedOperation, auditLogFromServerResponse.getOperation());
        assertEquals(expectedRemark, auditLogFromServerResponse.getRemarks());
        assertEquals(dateFormatter.parse(expectedAuditDate), auditLogFromServerResponse.getAuditDate());
    }

    @And("^the Maxx restful service database save audit log with Maxx user \"([^\"]*)\", operation is \"([^\"]*)\", audit time is at \"([^\"]*)\", and remark is \"([^\"]*)\"$")
    public void the_Maxx_restful_service_database_save_audit_log_with_Maxx_user_operation_is_audit_time_is_at_and_remark_is(String expectedMaxxUser, String expectedOperation, String expectedAuditDate, String expectedRemark) throws Throwable {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXX");
        List<AuditLog> auditLogs = auditLogService.findAll();

        assertEquals(1, auditLogs.size());
        AuditLog auditLog = auditLogs.get(0);
        assertEquals(expectedMaxxUser, auditLog.getMaxxUser());
        assertEquals(expectedOperation, auditLog.getOperation());
        assertEquals(expectedRemark, auditLog.getRemarks());
        assertEquals(dateFormatter.parse(expectedAuditDate), auditLog.getAuditDate());
    }

    @When("^the client post http post message to Maxx restful service \"([^\"]*)\"$")
    public void the_client_post_http_post_message_to_Maxx_restful_service(String auditLogUrl) throws Throwable {
        httpPostResponse = restTemplate.postForEntity(getBaseUrl() + auditLogUrl, auditLog, String.class);
    }

    @When("^the client fire http get message to \"([^\"]*)\" with Maxx user \"([^\"]*)\", start date \"([^\"]*)\", end date \"([^\"]*)\"$")
    public void the_client_fire_http_get_message_to_with_Maxx_user_start_date_end_date(String auditLogUrl, String maxxUser, String startDate, String endDate) throws Throwable {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXX");
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("maxxUser", maxxUser);
        urlVariables.put("startDate", dateFormatter.parse(startDate));
        urlVariables.put("endDate", dateFormatter.parse(endDate));

        httpGetResponse = restTemplate.getForObject(getBaseUrl() + auditLogUrl, String.class, urlVariables);
    }

    @Then("^the client receives http status code of (\\d+) and Json body with \"([^\"]*)\"$")
    public void the_client_receives_http_status_code_of_and_Json_body_with(int httpStatusCode, String auditLogId) throws Throwable {
        assertThat(httpPostResponse.getStatusCode().value(), is(httpStatusCode));
        assertThat(httpPostResponse.getBody(), containsString("auditLogId"));
    }
}
