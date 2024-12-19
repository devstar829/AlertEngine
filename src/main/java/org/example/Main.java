package org.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class Solution {
    public static void main(String[] args) {
        // This map contains test data returned by the client for different alert queries.
        // F.e. with the initial test data the client will return the following values:
        // - for the first three consecutive calls to client.executeQuery("test-query"): 11, 8, 11.
        // - for the first three consecutive calls to client.executeQuery("another-query"): 0, 0, 0.
        // Feel free to change the content of this map to help test that your solution
        // satisfies all requirements.
        Map<String, TestData> queryData = new HashMap<String, TestData>() {{
            put("test-query", new TestData(11, 8));
        }};
        Client client = new Client(queryData);

        List<Alert> alerts = client.getAlerts();

        // TODO: evaluate all alerts.
        Alert alert = alerts.get(0);

        // TODO: evaluate alert on an interval.
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        for(Alert alert1: alerts){
            scheduler.scheduleAtFixedRate(() -> evaluateAlert(client, alert), 0, alert1.intervalSeconds,
                    TimeUnit.SECONDS);
        }
    }

    // Method to evaluate an alert
    public static void evaluateAlert(Client client, Alert alert) {
        double value = client.executeQuery(alert.query);

        if (value > alert.criticalThreshold.value) {
            // If value exceeds the critical threshold, send a notification
            client.notify(alert.name, alert.criticalThreshold.message);
        } else {
            // If value is within the threshold, resolve the alert
            client.resolve(alert.name);
        }
    }

    public static class Alert {
        public final String name;
        public final String query;
        public final long intervalSeconds;
        public final Threshold criticalThreshold;

        public Alert(
                String name,
                String query,
                long intervalSeconds,
                Threshold criticalThreshold) {
            this.name = name;
            this.query = query;
            this.intervalSeconds = intervalSeconds;
            this.criticalThreshold = criticalThreshold;
        }
    }

    public static class Threshold {
        public final double value;
        public final String message;

        public Threshold(double value, String message) {
            this.value = value;
            this.message = message;
        }
    }

    // Fake implementation of alerts client that you are free to change to help test the program.
    public static class Client {
        static SimpleDateFormat formatter = new SimpleDateFormat("[HH:mm:ss] ");

        private final Map<String, TestData> queryData;

        public Client(Map<String, TestData> queryData) {
            this.queryData = queryData;
        }

        public List<Alert> getAlerts() {
            return List.of(new Alert(
                    "test-alert",
                    "test-query",
                    2 /* intervalSeconds */,
                    new Threshold(10.0, "critical-alert")));
        }

        public double executeQuery(String query) {
            TestData data = this.queryData.get(query);
            if (data != null) {
                return data.nextValue();
            }
            return 0;
        }

        public void notify(String alertName, String thresholdMessage) {
            log(String.format("notifying alert %s: %s", alertName, thresholdMessage));
        }

        public void resolve(String alertName) {
            log(String.format("resolving alert %s", alertName));
        }

        private void log(String msg) {
            String time = formatter.format(new Date());
            System.out.println(time + msg);
        }
    }

    public static class TestData {
        private int index;
        private final double[] values;

        public TestData(double... values) {
            this.index = 0;
            this.values = values;
        }

        private synchronized double nextValue() {
            int curIndex = this.index++;
            if (this.index >= this.values.length) {
                this.index = 0;
            }
            return this.values[curIndex];
        }
    }
}

