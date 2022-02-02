package com.example.demo;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentList;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Config;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class Namespace {

    public static String checkAccessibility(String config) throws IOException, ApiException {
        String status = null;
        ApiClient client = null;
        try {
            client = Config.fromConfig(config);
            if (client != null) {
                status = "success";
            }
        } catch (Exception e) {

            status = "failed";
        }

        return status;
    }

    public static ArrayList<HashMap> getAllNamespaces(String config) throws IOException, ApiException {
        ApiClient client = Config.fromConfig(config);
        client.setVerifyingSsl(false);
        Configuration.setDefaultApiClient(client);
        CoreV1Api api = new CoreV1Api();
        V1NamespaceList list = api.listNamespace(null, null, null, null, null, null, null, null, null);
        ArrayList<HashMap> namespaces = new ArrayList<HashMap>();
        for (V1Namespace item : list.getItems()) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("Name", item.getMetadata().getName());
            map.put("Status", item.getStatus().getPhase());
            LocalDate dt = LocalDate.of(item.getMetadata().getCreationTimestamp().getYear(), item.getMetadata().getCreationTimestamp().getMonthOfYear(), item.getMetadata().getCreationTimestamp().getDayOfMonth());
            LocalDate today = LocalDate.now();
            Long age = ChronoUnit.DAYS.between(dt, today);
            map.put("Age", age.toString() + "d");
            namespaces.add(map);
        }

        return namespaces;
    }
}

class Pod {

    public static ArrayList<HashMap> getpodfornamespace(String config, String ns) throws IOException, ApiException {

        ApiClient client = Config.fromConfig(config);
        client.setVerifyingSsl(false);
        Configuration.setDefaultApiClient(client);
        CoreV1Api api = new CoreV1Api();

        //String ns = "default";
        V1PodList list2 = api.listNamespacedPod(ns, null, null, null, null, null, null, null, null, null);
        ArrayList<HashMap> pod = new ArrayList<HashMap>();
        for (V1Pod item : list2.getItems()) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("Name", item.getMetadata().getName());
            //map.put("Status", item.getStatus().getPhase());

            ArrayList<String> status_check = new ArrayList<>();
            int rd = 0;
            for (int i = 0; i < item.getStatus().getContainerStatuses().size(); i++) {
                status_check.add(String.valueOf(item.getStatus().getContainerStatuses().get(i).getReady()));
            }
            boolean st = status_check.stream().allMatch(t -> t.toLowerCase().contains("true"));
            for (String i : status_check) {
                if (i == "true") {
                    rd++;
                }
            }
            map.put("Status", String.valueOf(st));
            String container_cnt = rd + "/" + item.getStatus().getContainerStatuses().size();
            map.put("Ready", container_cnt);
            Integer restartCounts = item.getStatus().getContainerStatuses().get(0).getRestartCount();
            map.put("Restarts", restartCounts.toString());
            map.put("Image", item.getStatus().getContainerStatuses().get(0).getImage());
            //Map<String, String> lb = item.getMetadata().getLabels();
            String LB = String.valueOf(item.getMetadata().getLabels());
            map.put("Labels", LB);
            LocalDate dt = LocalDate.of(item.getMetadata().getCreationTimestamp().getYear(),
                    item.getMetadata().getCreationTimestamp().getMonthOfYear(),
                    item.getMetadata().getCreationTimestamp().getDayOfMonth());
            LocalDate today = LocalDate.now();
            //Period period = Period.between(dt, today);

            //String age = period.getMonths() + " months " + period.getDays() + " days";
            Long age = ChronoUnit.DAYS.between(dt, today);
            map.put("Age", age.toString() + "d");
            pod.add(map);
        }
        return pod;
    }

    public static ArrayList<HashMap> getAllPodForAllNamespaces(String config) throws IOException, ApiException {
        ApiClient client = Config.fromConfig(config);
        client.setVerifyingSsl(false);
        Configuration.setDefaultApiClient(client);
        CoreV1Api api = new CoreV1Api();

        V1PodList list = api.listPodForAllNamespaces(null, null, null,
                null, null, null, null, null, null);
        ArrayList<HashMap> pod = new ArrayList<HashMap>();
        for (V1Pod item : list.getItems()) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("Name", item.getMetadata().getName());
            map.put("Namespace", item.getMetadata().getNamespace());
            //map.put("Status", item.getStatus().getPhase());

            ArrayList<String> status_check = new ArrayList<>();
            int rd = 0;
            for (int i = 0; i < item.getStatus().getContainerStatuses().size(); i++) {
                status_check.add(String.valueOf(item.getStatus().getContainerStatuses().get(i).getReady()));
            }
            boolean st = status_check.stream().allMatch(t -> t.toLowerCase().contains("true"));
            for (String i : status_check) {
                if (i == "true") {
                    rd++;
                }
            }
            map.put("Status", String.valueOf(st));
            String container_cnt = rd + "/" + item.getStatus().getContainerStatuses().size();
            map.put("Ready", container_cnt);

            Integer restartCounts = item.getStatus().getContainerStatuses().get(0).getRestartCount();
            map.put("Restarts", restartCounts.toString());

            LocalDate dt = LocalDate.of(item.getMetadata().getCreationTimestamp().getYear(),
                    item.getMetadata().getCreationTimestamp().getMonthOfYear(),
                    item.getMetadata().getCreationTimestamp().getDayOfMonth());
            LocalDate today = LocalDate.now();
            //Period period = Period.between(dt, today);

            //String age = period.getMonths() + " months " + period.getDays() + " days";
            Long age = ChronoUnit.DAYS.between(dt, today);
            map.put("Age", age.toString() + "d");
            pod.add(map);
        }
        return pod;
    }

    public static String getLogs(String name, String ns, String config) throws IOException, ApiException {
        ApiClient client = Config.fromConfig(config);
        client.setVerifyingSsl(false);
        Configuration.setDefaultApiClient(client);
        CoreV1Api api = new CoreV1Api();
        System.out.println( name + ns);
        String logs = api.readNamespacedPodLog(name, ns, null, null, null, null, null, null, null, null, null);
        return logs;
    }

}

class Node {

    public static ArrayList<HashMap> getAllNode(String config) throws IOException, ApiException {

        ApiClient client = Config.fromConfig(config);
        client.setVerifyingSsl(false);
        Configuration.setDefaultApiClient(client);
        CoreV1Api api = new CoreV1Api();
        V1NodeList list = api.listNode(null, null, null, null, null, null, null, null, null);
        ArrayList<HashMap> nodes = new ArrayList<HashMap>();

        for (V1Node item : list.getItems()) {
            HashMap<String, String> map = new HashMap<String, String>();
            // to get name of the node
            map.put("Name", item.getMetadata().getName());
            // to get status of the node
            map.put("status", item.getStatus().getConditions().get(3).getType());
            // to get labels 
            Map<String, String> lb = item.getMetadata().getLabels();
            map.put("Labels", lb.toString());

            // map.put("status",item.status();
            // to get version
            map.put("Version", item.getStatus().getNodeInfo().getKubeletVersion());
            // to get age of node
            LocalDate dt = LocalDate.of(item.getMetadata().getCreationTimestamp().getYear(), item.getMetadata().getCreationTimestamp().getMonthOfYear(), item.getMetadata().getCreationTimestamp().getDayOfMonth());
            LocalDate today = LocalDate.now();
            // Period period = Period.between(dt, today);
            // String age = period.getMonths() + " months " + period.getDays() + " days";
            Long age = ChronoUnit.DAYS.between(dt, today);
            map.put("Age", age.toString() + "d");

            nodes.add(map);
        }
        return nodes;
    }
}

class Deployment {

    public static ArrayList<HashMap> getAllDeployment(String config) throws IOException, ApiException {

        AppsV1Api api = new AppsV1Api();
        ApiClient client = Config.fromConfig(config);
        client.setVerifyingSsl(false);
        Configuration.setDefaultApiClient(client);

        V1DeploymentList list1 = api.listDeploymentForAllNamespaces(null, null, null, null, null, null, null, null, null);
        ArrayList<HashMap> deploy = new ArrayList<HashMap>();

        for (V1Deployment item : list1.getItems()) {

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("Name", item.getMetadata().getName());
            map.put("Namespace", item.getMetadata().getNamespace());
            Integer re = item.getStatus().getUpdatedReplicas();
            map.put("Up-To-Date", re.toString());
            Integer rep = item.getStatus().getAvailableReplicas();
            String container_cnt = null;
            if (rep != null) {
                map.put("Available", rep.toString());
                container_cnt = item.getStatus().getReadyReplicas() + "/" + item.getStatus().getAvailableReplicas();
            } else {
                map.put("Available", "0");
                container_cnt = "0/" + item.getStatus().getAvailableReplicas();
            }

            map.put("Ready", container_cnt);
            LocalDate dt = LocalDate.of(item.getMetadata().getCreationTimestamp().getYear(), item.getMetadata().getCreationTimestamp().getMonthOfYear(), item.getMetadata().getCreationTimestamp().getDayOfMonth());
            LocalDate today = LocalDate.now();
            Long age = ChronoUnit.DAYS.between(dt, today);
            map.put("Age", age.toString() + "d");
            deploy.add(map);
        }
        return deploy;
    }

    public static ArrayList<HashMap> getDeploymentForNamespace(String config, String ns) throws IOException, ApiException {

        AppsV1Api api = new AppsV1Api();
        ApiClient client = Config.fromConfig(config);
        client.setVerifyingSsl(false);
        Configuration.setDefaultApiClient(client);
        V1DeploymentList list2 = api.listNamespacedDeployment(ns, null, null, null, null, null, null, null, null, null);
        ArrayList<HashMap> deploys = new ArrayList<HashMap>();
        for (V1Deployment item : list2.getItems()) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("Name", item.getMetadata().getName());

            Integer re = item.getStatus().getUpdatedReplicas();
            map.put("Up-To-Date", re.toString());

            Integer rep = item.getStatus().getAvailableReplicas();
            String container_cnt = null;
            if (rep != null) {
                map.put("Available", rep.toString());
                container_cnt = item.getStatus().getReadyReplicas() + "/" + item.getStatus().getAvailableReplicas();
            } else {
                map.put("Available", "0");
                container_cnt = "0/" + item.getStatus().getAvailableReplicas();
            }
            map.put("Ready", container_cnt);
            LocalDate dt = LocalDate.of(item.getMetadata().getCreationTimestamp().getYear(),
                    item.getMetadata().getCreationTimestamp().getMonthOfYear(),
                    item.getMetadata().getCreationTimestamp().getDayOfMonth());
            LocalDate today = LocalDate.now();
            Long age = ChronoUnit.DAYS.between(dt, today);
            map.put("Age", age.toString() + "d");
            deploys.add(map);
        }
        return deploys;
    }
}

public class NewClass {

    public static void main(String[] args) throws IOException, ApiException {
//        ApiClient client = Config.defaultClient();

//        ArrayList<HashMap> namespaces = Namespace.getAllNamespaces();
//        ArrayList<HashMap> pod = Pod.getpodfornamespace("default");
//        ArrayList<HashMap> Allpod = Pod.getAllPodForAllNamespaces();
//        ArrayList<HashMap> node = Node.getAllNode();
//        System.out.println("Namespaces: " + namespaces);
//        System.out.println("Pods: " + Allpod);
//         System.out.println("Nodes: " + node);
    }
}
