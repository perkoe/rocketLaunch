# Rocket Launch Notifier

## Introduction
The Rocket Launch Notifier is a simple application that checks for upcoming rocket launches and sends email notifications if a launch is scheduled within the next week, if the launch date changes, or if the launch is canceled.

# Prerequisites
Before running the program, ensure that you have the following prerequisites installed on your system:

Java Development Kit (JDK) 19 or higher
Maven (for building the project)
An SMTP server for sending email notifications

# Configuration
Clone the repository to your local machine: git clone https://github.com/example/rocket-launch-notifier.git

Navigate to the project directory: cd rocket-launch-notifier
Open the application.properties file located in the src/main/resources directory.
Configure the SMTP server settings, including the host, port, username, password, and sender email address.
Save and close the application.properties file.

# Email Output Example
Below is an example of the email notification sent by the Rocket Launch Notifier:
<img width="566" alt="image" src="https://github.com/perkoe/rocketLaunch/assets/105124464/2728b72e-54f7-48a3-a867-7180ead7db19">

and email preview: 

<img width="781" alt="image" src="https://github.com/perkoe/rocketLaunch/assets/105124464/282228cf-65e0-49d7-ba80-be5efc66698e">

# Customization
You can customize the recipients of the email notifications by modifying the email-list.yaml file in the resources folder.

