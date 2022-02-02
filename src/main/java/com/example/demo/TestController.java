package com.example.demo;

import com.example.demo.entity.Cluster;
import com.example.demo.entity.Users;
import com.example.demo.service.ClusterService;
import com.example.demo.service.UsersService;
import io.kubernetes.client.openapi.ApiException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class TestController {

    @Autowired
    ClusterService clusterService;

    @Autowired
    UsersService usersService;

    private String message;

    @Value("${tomtom.apikey}")
    private String tomTomApiKey;

    private List<Location> coolLocations() {
        
        List<Cluster> clusters = clusterService.getAllCluster();
        ArrayList<Location> l = new ArrayList<Location>();
        for (int i = 0; i < clusters.size(); i++) {
            double i1 = clusters.get(i).getLongitude();
            double i2 = clusters.get(i).getLatitude();
            String i3 = clusters.get(i).getName();
            l.add(new Location(new double[]{i1, i2}, i3));
            
           
//        return List.of(
//                new Location(new double[]{139.6503, 35.6762}, "Tokyo CDC"),
//                new Location(new double[]{135.1956, 34.6901}, "Kobe CDC"),
//                new Location(new double[]{135.5023, 34.6937}, "Osaka CDC")
//        );
        }
        return l;
    }

    private static class Location {

        private final double[] lnglat;
        private final String description;

        public Location(double[] lnglat, String description) {
            this.lnglat = lnglat;
            this.description = description;
        }

        public double[] getLnglat() {
            return lnglat;
        }

        public String getDescription() {
            return description;
        }
    }

    @GetMapping("/view-clusters")
    public String index(Model model) throws IOException, ApiException {

        HashMap<String, String> cluster_configs = new HashMap<String, String>();
        
        HashMap<String, String> cluster_accessibilty = new HashMap<String, String>();
        List<Cluster> clusters = clusterService.getAllCluster();
        for (int i = 0; i < clusters.size(); i++) {
            String path = (clusters.get(i).getConfig());
            String content = new String(Files.readAllBytes(Paths.get(path)));
            cluster_configs.put(clusters.get(i).getName(), content);
            cluster_accessibilty.put(clusters.get(i).getName(), Namespace.checkAccessibility(path));
        }
        Comparator<Cluster> compareByGroup = 
	(Cluster o1, Cluster o2) -> o1.getGroup().compareTo( o2.getGroup() );

        Collections.sort(clusters, compareByGroup);
        model.addAttribute("clusters", clusters);
        model.addAttribute("configs", cluster_configs);
        model.addAttribute("access", cluster_accessibilty);
        model.addAttribute("apikey", tomTomApiKey);
        model.addAttribute("coolLocations", coolLocations());
        return "index"; //view
    }

    @GetMapping("/login")
    public String login(Model model) throws IOException, ApiException {

        return "login"; //view
    }

    @PostMapping("/login")
    public String authenticate(@ModelAttribute Users user, Model model)
            throws IOException, IllegalStateException, ApiException {

        String validate = usersService.authenticate(user.getUsername(), user.getPassword());
        System.out.println(user.getUsername() + user.getPassword() + validate);
        if (validate.equals("Success")) {
           
           return("redirect:/view-clusters");
        } else {
            return "login";
        }
    }

    @GetMapping("/home")
    public String main(Model model) throws IOException, ApiException {

        HashMap<String, String> cluster_configs = new HashMap<String, String>();
        List<Cluster> clusters = clusterService.getAllCluster();
        for (int i = 0; i < clusters.size(); i++) {
            String path = (clusters.get(i).getConfig());
            String content = new String(Files.readAllBytes(Paths.get(path)));
            cluster_configs.put(clusters.get(i).getName(), content);
        }
        model.addAttribute("clusters", clusters);
        model.addAttribute("configs", cluster_configs);
        return "cluster"; //view
    }

    @GetMapping("/register-cluster")
    public String home(Model model) {
        return "index";
    }

    @PostMapping("/register-cluster")
    public String uploadFiles(@RequestParam("file") MultipartFile file, @ModelAttribute Cluster cluster, Model model)
            throws IOException, IllegalStateException {
        String baseDir = "/Users/bipin.gowda/Downloads/demo/src/main/resources/kubeConfigFiles/";
        file.transferTo(new File(baseDir + cluster.getName() + "_" + file.getOriginalFilename()));
        String p = baseDir + cluster.getName() + "_" + file.getOriginalFilename();
        cluster.setConfig(p);
        if(cluster.getType().equals("CDC")){
            cluster.setGroup("0");
        }
        else if(cluster.getType().equals("RDC")){
            cluster.setGroup("1");
        }
        else if(cluster.getType().equals("GC")){
            cluster.setGroup("2");
        }
        else{
            cluster.setGroup("3");
        }
        clusterService.addNew(cluster);
        return("redirect:/view-clusters");

    }

    @GetMapping("/namespaces")
    public String namespace(
            @RequestParam(value = "cluster", required = false) String selectedCluster,
            Model model) throws IOException, ApiException {

        ArrayList<String> headings = new ArrayList<String>();

        int c = 0;
        List<Cluster> clusters = clusterService.getAllCluster();
        model.addAttribute("clusters", clusters);
        for (int i = 0; i < clusters.size(); i++) {

            if (clusters.get(i).getName().equals(selectedCluster)) {
                c = i;
                break;
            }
        }
        String config = clusters.get(c).getConfig();
        ArrayList<HashMap> namespaces = Namespace.getAllNamespaces(config);
        model.addAttribute("clustername", clusters.get(c).getName());
        headings.add("Name");
        headings.add("Status");
        headings.add("Age");
        model.addAttribute("headings", headings);
        model.addAttribute("resource", namespaces);
        model.addAttribute("ns", namespaces);
        model.addAttribute("resourceHead", "Namespaces");
        return "resources"; //view
    }

    @GetMapping("/pods")
    public String pod(
            @RequestParam(value = "namespace", required = false) String selectedNamespace,
            @RequestParam(value = "cluster", required = false) String selectedCluster,
            Model model) throws IOException, ApiException {

        ArrayList<String> headings = new ArrayList<String>();
        HashMap<String, String> logs = new HashMap<String, String>();
        int c = 0;
        List<Cluster> clusters = clusterService.getAllCluster();
        model.addAttribute("clusters", clusters);
        for (int i = 0; i < clusters.size(); i++) {

            if (clusters.get(i).getName().equals(selectedCluster)) {
                c = i;
                break;

            }
        }
        String config = clusters.get(c).getConfig();
        ArrayList<HashMap> namespaces = Namespace.getAllNamespaces(config);
        model.addAttribute("clustername", clusters.get(c).getName());
        headings.add("Name");
        headings.add("Ready");
        headings.add("Restarts");
        headings.add("Status");
        headings.add("Age");
        if (selectedNamespace != null) {
            ArrayList<HashMap> pods = Pod.getpodfornamespace(config, selectedNamespace);
            model.addAttribute("resource", pods);
            model.addAttribute("namespace", selectedNamespace);
            
            for(int i=0; i<pods.size(); i++)
            {
                logs.put(pods.get(i).get("Name").toString() , Pod.getLogs(pods.get(i).get("Name").toString(), selectedNamespace, config));
                
            }
            
        } else {
            ArrayList<HashMap> pods = Pod.getAllPodForAllNamespaces(config);
            headings.add("Namespace");
            model.addAttribute("resource", pods);
            
            for(int i=0; i<pods.size(); i++)
            {
                logs.put(pods.get(i).get("Name").toString() , Pod.getLogs(pods.get(i).get("Name").toString(), pods.get(i).get("Namespace").toString(), config));
                
            }
            
        }
        headings.add("Logs");
        model.addAttribute("logs", logs);

        model.addAttribute("headings", headings);

        model.addAttribute("ns", namespaces);
        model.addAttribute("resourceHead", "Pods");
        return "resources"; //view
    }

    @GetMapping("/cluster-resources")
    public String resources(
            @RequestParam(value = "namespace", required = false) String selectedNamespace,
            @RequestParam(value = "cluster", required = false) String selectedCluster,
            Model model) throws IOException, ApiException {

        int c = 0;
        List<Cluster> clusters = clusterService.getAllCluster();
        model.addAttribute("clusters", clusters);
        for (int i = 0; i < clusters.size(); i++) {
            if (clusters.get(i).getName().equals(selectedCluster)) {
                c = i;
                break;
            }
        }
        String config = clusters.get(c).getConfig();
        ArrayList<HashMap> namespaces = Namespace.getAllNamespaces(config);
        model.addAttribute("clustername", clusters.get(c).getName());

        int no_of_deployments = Deployment.getAllDeployment(config).size();
        int no_of_nodes = Node.getAllNode(config).size();
        int no_of_ns = Namespace.getAllNamespaces(config).size();
        model.addAttribute("no_of_deploys", no_of_deployments);
        model.addAttribute("no_of_nodes", no_of_nodes);
        model.addAttribute("no_of_ns", no_of_ns);
        model.addAttribute("ns", namespaces);
        return "resources"; //view
    }

    @GetMapping("/deployment")
    public String deployment(
            @RequestParam(value = "namespace", required = false) String selectedNamespace,
            @RequestParam(value = "cluster", required = false) String selectedCluster,
            Model model) throws IOException, ApiException {

        ArrayList<String> headings = new ArrayList<String>();
        int c = 0;
        List<Cluster> clusters = clusterService.getAllCluster();
        model.addAttribute("clusters", clusters);
        for (int i = 0; i < clusters.size(); i++) {

            if (clusters.get(i).getName().equals(selectedCluster)) {
                c = i;
                break;
            }
        }
        String config = clusters.get(c).getConfig();
        ArrayList<HashMap> namespaces = Namespace.getAllNamespaces(config);
        model.addAttribute("clustername", clusters.get(c).getName());
        headings.add("Name");
        headings.add("Up-To-Date");
        headings.add("Available");
        headings.add("Ready");
        if (selectedNamespace != null) {
            ArrayList<HashMap> deploy = Deployment.getDeploymentForNamespace(config, selectedNamespace);
            model.addAttribute("resource", deploy);
            model.addAttribute("namespace", selectedNamespace);
        } else {
            ArrayList<HashMap> deploy = Deployment.getAllDeployment(config);
            headings.add("Namespace");
            model.addAttribute("resource", deploy);
        }

        model.addAttribute("headings", headings);

        model.addAttribute("ns", namespaces);
        model.addAttribute("resourceHead", "Deployments");
        return "resources"; //view
    }

    @GetMapping("/nodes")
    public String node(
            @RequestParam(value = "cluster", required = false) String selectedCluster,
            Model model) throws IOException, ApiException {

        ArrayList<String> headings = new ArrayList<String>();
        int c = 0;
        List<Cluster> clusters = clusterService.getAllCluster();
        model.addAttribute("clusters", clusters);
        for (int i = 0; i < clusters.size(); i++) {

            if (clusters.get(i).getName().equals(selectedCluster)) {
                c = i;
                break;
            }
        }
        String config = clusters.get(c).getConfig();
        ArrayList<HashMap> nodes = Node.getAllNode(config);
        ArrayList<HashMap> namespaces = Namespace.getAllNamespaces(config);
        model.addAttribute("clustername", clusters.get(c).getName());
        headings.add("Name");
        headings.add("Version");
        headings.add("Status");
        headings.add("Age");
        model.addAttribute("headings", headings);
        model.addAttribute("resource", nodes);
        model.addAttribute("ns", namespaces);
        model.addAttribute("resourceHead", "Nodes");
        return "resources"; //view
    }
}
