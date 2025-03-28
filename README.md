1) Install gradle and add it to path. 
2) Create a config.properties file in app/src/main/resources which contains:
db.url=(url)
db.username=(username)
db.password=(password)
3) Do ./gradlew build and then ./gradlew run (THIS IS FOR WINDOWS. READ DOCS FOR LINUX EQUIVALENT)

Note: This project is using Java Swing for UI implementation.