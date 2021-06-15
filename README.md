# SchoolMax
A Discord SchoolMax Bot that logins to SchoolMax, list all the classes, check for one's next year's schedule, and check one's grade.

To add SchoolMax bot to your server: https://discord.com/api/oauth2/authorize?client_id=846623220954169384&permissions=8&scope=bot

Commands: 
  1. -command
  2. -login
  3. -list
  4. -check 
  5. -info
  6. -NextYear
  7. -getAllQuarter
  8. -getAll

Uses:
  * -command -> to show all the commands of the bot
  * -login -> to login to your SchoolMax account (you only have to login once)
      * Format: -login + username + password 
      * Example: -login john-smith abc12345
  * -list -> to show all your classes (you must login to do so)
  * -check -> to check the term and overall grade of your class of choice by using the course number (provided by "-list" command)
      * Format: -check courseNumber
      * Example: -check 123141-3
  * -info -> to see all the information given by the developer
  * -NextYear -> to check your next year's schedule
  * to get your quarterly grade for all your classes
  * to get your overall grade for all your classes
