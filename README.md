# KEN Group 05 - Project 1-2 (KEN1600)
Welcome to the GitLab repository for Project 1-2!


### Running the project

##### From VSCode
Ensure the [Java Extension Pack](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) is installed. Open the folder containing the project using VSCode, and wait for the Maven integration plugin to load. A tab called `Maven` will appear. Expand it, and navigate to `app > Plugins > javafx > run` and click on the Play button next to it.

##### From Intellij Idea
Open the folder containing the project in Idea. In the top right corner, expand the dropdown, and select `Edit Configurations`. Click on the + in the top left to create a new Run Configuration, and select maven in the new dropdown. Once the run configuration has been created, give it a name you like. Under run, edit the `Command Line` field to say `javafx:run`. Press apply and OK. Select the run configuration from the dropdown, and press the green play button.

##### From the terminal
Ensure Java is installed on your system.

###### Windows
Open a terminal in the folder containing the project. Use the following command.
```bash
./mvnw.cmd javafx:run
```

###### MacOS & Linux
Open a terminal in the folder containing the project. Use the following command.
```bash
./mvnw javafx:run
```