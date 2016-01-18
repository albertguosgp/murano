Feature:
  As a Maxx restful service user,
  I want to use Maxx restful service to save and search audit log,
  so that I can keep and provide audit log to compliance officer.

  @cleanup
  Scenario: As a Maxx restful service user, I want to use Maxx restful service call to save audit log.
    Given maxx user is "Johndoe", operation is "Disable HK sales firm", audit time is at "2010-11-01T13:23:12Z", and remark is "Firm not used"
    When the client post http post message to Maxx restful service "/auditlog"
    Then the client receives http status code of 200 and Json body with "auditLogId"
    And the Maxx restful service database save audit log with Maxx user "Johndoe", operation is "Disable HK sales firm", audit time is at "2010-11-01T13:23:12Z", and remark is "Firm not used"
    When the client fire http get message to "/auditlog" with Maxx user "Johndoe", start date "2009-11-01T13:23:12Z", end date "2011-11-01T13:23:12Z"
    Then the audit log server response should be maxx user is "Johndoe", operation is "Disable HK sales firm", audit time is at "2010-11-01T13:23:12Z", and remark is "Firm not used"
