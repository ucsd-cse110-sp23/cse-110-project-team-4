# SayIt Assistant
#### By Agile Lovers

*Team 4: Louie Cai, Anish Govind, Lilian Kong, Nicholas Lam, Billy Phan, Shera Zhong*

---

## Instructions to run
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
![app](./assets/sayitassistant.png)

---

# How to use the "SayItAssistant" app
Generating an AI generated response to a recorded query (question/statement). 

The purpose of the **SayItAssistant** application is to let the user speak out loud their question or statement. Within seconds, a response should be displayed on the screen according to their question or statement. 

On our application, there are several buttons the user should know about:
1. "New Question"
   * the button initiates a recording for the user 
   * The user should start speaking their question or statement 
   * Once the user is finished with their query, click "End Recording"
2. "Delete Question"
   * The user should click on the question they wish to delete
   * Once the user has selected their question, click on the "Delete Question" button
3. "Clear All"
   * Clears all questions from the question list and no questions should be displayed