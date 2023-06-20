# SayIt Assistant
#### By Agile Lovers

*Team 4: Louie Cai, Anish Govind, Lilian Kong, Nicholas Lam, Billy Phan, Shera Zhong*

## Documentations
- [Frontend/UI Code Documentation](./docs/frontend.md)

## Prerequisite installations
Our project is running on **JDK 20** and utilizes **Maven** as our package manager. Please make sure you have these installed on your machine before running our project ( *If you're running Linux or MacOS, you can use Homebrew to install Maven (https://maven.apache.org))*.

## Instructions to run

In other to build and run our project, install our project by:
1. click on `Code`
2. click on `Download ZIP`

Next, go to the default download directory and unzip the project file into a directory of your choosing. 

Open your terminal and go to the directory where you unzipped the file. Run the following command:
```
mvn javafx:run -f pom.xml
```

Once you've run this command, a window should appear on your screen. It should look like:
![app](./assets/sayitassistant.png)

---

# How to use the "SayItAssistant" app
Generating an AI generated response to a recorded command (prompt/statement). 

The purpose of the **SayItAssistant** application is to let the user speak out loud their prompt or statement. Within seconds, a response should be displayed on the screen according to their prompt or statement. 

On our application, there are 6 commands that the user should know about:
1. "Question"
   * Click on the start button and give the command
   * After the command, the user should start asking their question
   * the SayItAssistant app will generate an answer for the question and store it in the database
2. "Delete Prompt"
   * The user should click on the prompt they wish to delete before giving the command
   * Once the user has selected their prompt, click on the start button and give the command
   * This will delete the selected prompt
3. "Clear All"
   * Click on the start button and give the command
   * This will clear all the prompts and responses on the screen and in the database
4. "Setup Email"
   * Click on the start button and give the command
   * A window will pop up and give the user the option to fill or update in the information for their email configurations
   * The user will be able to save or discard any changes they make
5. "Create Email"
   * Click on the start button and give the command
   * After the command, the user should give the prompt for the email they want to generate
   * the SayItAssistant app will generate an email based on the provided prompt and sign it with the user's given display name
6. "Send Email"
   * The user should click on the email draft (created email) from the prompt history that they want to send
   * Once the user has selected their email draft, click on the start button and give the command as well as the recipeint email address
   * the SayItAssistant app will attempt to send the email to the recipient and display either a success message or an error message depending on the result

---

## Milestone 2 Delivery Checklist

#### Software design – 10 points
- [ ] Tidy code (indented, naming conventions, file/method-level comments as needed)
- [ ] – Extensive application of SRP and DRY
- [ ] – Evidence of OOD (objects and messages sounds like requirements)
- [ ] – Appropriate use of design patterns and/or dependency inversion to achieve OCP and SRP
- [ ] – Adapter/Mock of local Web API to support testing server functionality

#### Demo: satisfaction of all milestone requirements – 35 points [priority] - points:

- [ ] Story 8: Scalable to work on multiple platforms [H] - 2 pts (moved from MS1)
  - As a user I want an app that would eventually be ported to multiple platforms so that I can use the program from any device

- [ ] Story 8a: Display error message if server is unavailable [H] - 1 pts (part of #8 from MS1)
  - As a user I want the app to display an error message if the server is unavailable so that I know why the app isn’t working

- [ ] Story 9: Create account [H] - 2 pts
  - As a user I want to be able to create an account using my email and password so that I am able to log in to my account

- [ ] Story 10: Login [H] - 2 pts
  - As a user I want to be able to log in to my account so that I am the only one able to access my prompt history

- [ ] Story 10a: Automatic login [L] - 1 pts
  - As a user I want to be able to automatically log in to my account so that I do not need to login to access my prompt history

- [ ] Story 11: Access prompt history from multiple devices [H] - 2 pts 
  - As a user I want to be able to view a list of past questions on multiple devices so that I can see all the questions I asked no matter my location

- [ ] Story 12: Use voice commands instead of buttons [H] - 2 pts 
  - As a user I want to be able to use a single button and voice commands instead of using a lot of buttons so that the app is easier to use and has less components on the screen

- [ ] Story 12a: Ask question using voice command [H] - 2 pts 
  - As a user I want to be able to give a voice command followed by the question so that the app is easier to use

- [ ] Story 12b: Delete prompt using from prompt history using voice command [M] - 2 pts
  - As a user I want to be able to give a voice command to delete the currently selected prompt from the prompt history so that the app is easier to use

- [ ] Story 12c: Clear all prompts from prompt history using voice command [M] - 2 pts
  - As a user I want to be able to give a voice command to clear all prompts from the history so that the app is easier to use

- [ ] Story 13: Command shows up in the question prompt history [M] - 2 pts
  - As a user I want to know see the command in front of the question in the prompt history so that I can see what command was given for the prompt

- [ ] Story 14: Setup email screen [H] - 2 pts
  - As a user I want to a screen be able to edit the SMTP and email settings so that I can set up my email settings to send emails

- [ ] Story 14a: Setup email voice command [M] - 2 pts
  - As a user I want to be able to give a voice command to open the setup screen so that I can set up my email settings to send emails

- [ ] Story 14b: Access setup email from multiple devices [L] - 2 pts 
  - As a user I want to be able edit email settings from multiple devices so that I can fix my email settings regardless of which device I’m using

- [ ] Story 15: Create email voice command [H] - 2 pts
  - As a user I want to be able to give a voice command to create an email in ChatGPT and display the email so that I don’t have to type in my email

- [ ] Story 15a: Name added to bottom of email [M] - 2 pts
  - As a user I want my name from the email settings to appear under the closing of the email so that I don’t have to edit the email to add my name

- [ ] Story 15b: Create email command and results shows up in the prompt history [M] - 2 pts
  - As a user I want to know see the command in front of the email results in the prompt history so that I can see what command was given for the prompt

- [ ] Story 16: Send email voice command [H] - 2 pts
  - As a user I want to be able to send the currently selected email using a voice command to the specified email address so that I don’t have to copy and paste the email to send it

- [ ] Story 16a: Send email results should be displayed [M] - 2 pts
  - As a user I want to know see the results of the send email command so that I can see whether the email was successfully sent or if it failed to send

- [ ] Story 16b: Send email command and results shows up in the prompt history [M] - 2 pts
  - As a user I want to know see the command in front of the send email results in the prompt history so that I can see what command was given for the prompt


#### Testing – 24 points
- [ ] – Automated Story Testing - at least one BDD scenario per story (with JUnit, etc.)
- [ ] – App/classes designed for testability/demo (Mocking for local Web API))
- [ ] – Local testing: All tests automated, tied into JUnit (run and show pass/fail)
- [ ] – Continuous Integration: All non-instrumented tests run on GitHub Actions CI


#### Github Project – 4 points
- [ ] – Github Project tidy (all items are where they belong, in right order)
- [ ] – Tasks assigned to developers
- [ ] – Burn down chart looks good (steady progress, not all work at end)

#### GitHub – 5 points
- [ ] – Protected master branch (pull requests passed tests on GitHub Actions CI and passed code review)
- [ ] – Written code reviews for all merged Story branches
- [ ] – Merged protected branch story-by-story
- [ ] – Each push labelled with its Github issue number
