# SayIt Assistant
By Agile Lovers

---

# Instructions to run
In other to build and run our project, install our project by:
1. click on `Code`
2. click on `Download ZIP`

Next, go to the default download directory and unzip the project file into a directory of your choosing. 

Open your terminal and go to the directory where you unzipped the file. Run the following command:
```
mvn javafx:run -f pom.xml
```

*Note: Make sure you have `Maven` installed to run this command. If you're running Linux or MacOS, you can use Homebrew to install Maven (https://maven.apache.org).*

Once you've run this command, a window should appear on your screen. It should look like:
[insert image]

---

# How to use the "SayItAssistant" app
Generating an AI generated response to a recorded query (question/statement). 

The purpose of the **SayItAssistant** application is to let the user speak out loud their question or statement. Within seconds, a response should be displayed on the screen according to their question or statement. 

On our application, there are several buttons the user should know about:
1. "New Question"
    a. The button initiates a recording for the user
    b. The user should start speaking their question or statement
    c. Once the user is finished with their query, click "End Recording"
3. "Delete Question"
    a. The user should click on the question they wish to delete
    b. Once the user has selected their question, click on the "Delete Question" button
5. "Clear All"
    a. Clears all questions from the question list and no questions should be displayed

