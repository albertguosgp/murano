Feature:
  As a Maxx restful service user,
  I want to be able to query service and receive audit log report in CSV format
  So that compliance officer could see audit log.

  @cleanup
  Scenario: Receive audit log in mail box
    Given maxx user is "Johndoe", operation is "Disable HK sales firm", audit time is at "2010-11-01T13:23:12Z", and remark is "Firm not used"
    When the client post http post message to Maxx restful service "/auditlog"
    Then the client receives http status code of 200 and Json body with "auditLogId"
    And the Maxx restful service database save audit log with Maxx user "Johndoe", operation is "Disable HK sales firm", audit time is at "2010-11-01T13:23:12Z", and remark is "Firm not used"
    When the client post http post message to "/report/auditlog" with maxx user "Johndoe", start date "2010-11-01T13:23:11Z", end date is "2010-11-01T13:23:13Z", and reply to "support@flextrade.com"
    Then the client should receive audit log csv in email account "support@flextrade.com" after 6 seconds