The exercise requires you to develop an alert execution engine that executes alerts at the specified interval checks and sends notifications if alerts fire. Some engine basics:

- The alert engine is coded against an alerts client that provides access to alert configurations, querying, and notifications. We provide an API interface and a corresponding fake client implementation below.
    - You are free to change the fake implementation to test your engine, e.g., changing the alerts returned, or the results of ExecuteQuery.
- Alert configurations are read using the GetAlerts API. You can assume alerts will not change over time, so only need to be loaded once at start.
- One alert execution cycle involves:
    - Making an API call (ExecuteQuery) to query the value of the metric
    - Comparing the return value against Critical thresholds
    - Determining the state of the alert
    - Make a Notify API call if the alert is in CRITICAL state
    - Make a Resolve API call if the alert is transitioning to PASS state
- Alert can be in different states based on the current value of the query and the thresholds
    - It is considered PASS if value <= critical threshold
    - It is considered CRITICAL if value > critical threshold
- The alert engine should evaluate multiple alerts, ensuring that alert execution cycles are independent – one alert that’s slow to evaluate shouldn’t impact the scheduling of a different alert.
