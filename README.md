# SchoolMax
A Discord SchoolMax Bot that logins to SchoolMax, list all the classes, check for one's next year's schedule, and check one's grade.

To add SchoolMax bot to your server: https://discord.com/api/oauth2/authorize?client_id=846623220954169384&permissions=8&scope=bot

Commands: 
  1. -command
  2. -login + username + password
  3. -list
  4. -check + course number
  5. -info
  6. -NextYear
  7. -getAllQuarter
  8. -getAll
  9. -list . + year
  10. --getAllQuarter
  11. --getAll
  12. --getAll . + year

Uses:
  * -command -> to show all the commands of the bot
  * -login + username + password -> to login to your SchoolMax account (you only have to login once)
      * Format: -login + username + password 
      * Example: -login john-smith abc12345
      * ![Optional Text](SchoolMax/Examples/login.PNG)
  * -list -> to show all your classes (you must login to do so)
  * -check + course number -> to check the term and overall grade of your class of choice by using the course number (provided by "-list" command)
      * Format: -check courseNumber
      * Example: -check 123141-3
  * -info -> to see all the information given by the developer
  * -NextYear -> to check your next year's schedule
  * -getAllQuarter -> to get your quarterly grade for all your classes
  * -getAll -> to get your overall grade for all your classes
  * -list . + year -> to check your schedule for a particular year
  *  --getAllQuarter -> to show your quarterly grade in the server
  *  --getAll -> to show your yearly grade in the server
  *  --getAll . + year -> to show your yearly grade in the server for a particular year
