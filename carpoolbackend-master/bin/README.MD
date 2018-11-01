## Running the project

Run the docker-compose.yml (can be found in the docker folder on this repo) file to set up your environment:

```
docker stack deploy --compose-file=docker-compose.yml prod
```

* We don't type `docker-compose up` because we are using Docker Swarm.

In order to run the project make sure you have IntelliJ Idea Community or Ultimate (preferably) edition. As a student you can request a licence on http://www.jetbrains.com/student/. After installation, import the project from this git repo. Make sure you have Maven installed or use the one embedded in IntelliJ. 

To run the app create a Maven profile and add the following line:
```
clean install spring-boot:run
```

To package the app in order to use it for the docker image, use the following Maven command:
```
clean install spring-boot:repackage
```
## Working on this repo
Please make your own branch and merge it with develop when your work is done. 
