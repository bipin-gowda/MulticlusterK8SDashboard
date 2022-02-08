# MulticlusterK8sDashboard

This is a spring boot project which helps manage a multicluster kubernetes environment effectively with a unified dashboard.

Once you have downloaded this project a few changes are required before you can get it up and running,

**1. Ensure minkube is running on your local system -**
    
    You can use `Kubectl get pods` to verify, if it is not working then you can use `minikube start` to start minikube if it has been installed properly.

**2. Open application.properties file -** 

    Add your TomTom API key for the variable '﻿tomtom.apikey'.
    
    Go to https://developer.tomtom.com/ to create a new account and generate your API key.

    Verify all the MongoDB parameters and modify the port or database name based on your configuration.

**3. Open demo/src/main/java/com/example/demo/TestController.java -**

    Modify the value of the variable baseDir in line 128. This will be the location where cluster config files will be stored during registration.

**4. Open demo/src/main/resources/templates/webssh.html -** 

    Modify the username and password at line number 18 and 19. This should be the credentials of your machine which will be used to access the terminal.

Once the above changes are done, you can proceed to build and run this project on any IDE.

./mvnw spring-boot:run

The UI can be accessed at http://localhost:8080/login

    Username/password: admin/admin.


-----------------------------------------------------------------------------------------------------


**References -** 

UI template taken from - https://dashboardpack.com/theme-details/architectui-html-dashboard-free/

Map component developed with reference from https://github.com/tedyoung/tomtom-maps

Terminal component developed with reference from https://github.com/NoCortY/WebSSH
