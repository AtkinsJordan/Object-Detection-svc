# Object-Detection-svc
This service will ingest user images, analyze them for object detection, and return the enhanced content.

This project is a gradle  project.  If the project is opened in intelliJ, you can use the plugin to build the project. 
It can also be built using a cli with the following command `./gradlew`

For this project I chose to us MYSQL for the persistence database.  For assistance setting a MYSQL connection in 
IntelliJ you can visit the following https://www.jetbrains.com/help/idea/mysql.html.
In the application.properties file you may have to update the username and password to what you have for your connection.

In the data.sql file there are commands that can be used to populate the dbs.  In IntelliJ click on a statement.  Then
click the green play button above the number 1 for line 1, highlight the entire file and click again.

A postman collection JSON can be located at 
Object-Detection/src/test/java/com/example/Object/Detection/postman/Image Detection.postman_collection.json
This postman collection can be imported into postman to test the functionality of the API.  For the POST request an image 
can be uploaded from the user.  I have an image saved from the project that can be found 
at Object-Detection/src/main/resources/images/imagesforpostman/stone-fruit-module.jpg 

