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

##Starting the websocket
You need to expose the mongodb and neo4j ports in order to start the application. 

The following commands should be used :

Mongo:
docker run --name mongodb -p 27017:27017 -d mongo

Neo4j:
docker run  --publish=7474:7474 --publish=7687:7687 --volume=$HOME/neo4j/data:/data neo4j

