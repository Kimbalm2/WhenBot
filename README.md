## WhenBot
A Discord bot that keeps track of when your freinds are available. Whether you use it to find time to game or use it to find time to meet up, ask WhenBot for their schedule.

## Motivation
My friends and I work very different schedules so it can be hard to find times where we are all free. One works for an Airline, so his free time can change randomly. Another works at a restaurant so his schedule will change every week. and I work a typical 9-5. I wanted to make something we could use that would keep track of our availabilty, that way we can stop blasting our group chats and just ask my bot.


## Screenshots
TODO: demo screenshot 

## Tech used
<b>Built with</b>
- Java
- [Javacord Library](https://javacord.org/wiki/#structure-of-the-wiki) For easy interfacing with the Discord API
  - [Discord API] (https://discord.com/developers/docs/topics/certified-devices) 
- IntelliJ
- Gradle


## How to use?
- Invite my bot to your server: (Comming Soon!)

# Commands 
(Note: all times are in 24 hr time, so 13:00 would be equal to 01:00 PM)

<b>!when:</b> This command will output all of the free times that x users share.

	Usage:  !when [user1] [user2] [user x]
	Example: !when myFriendPedro MarcusFenix

<b>!setSchedule:</b> Create a new availability schedule by listing all the times you are available.

    Format: !setSchedule DAY-HH:mm-HH:mm,HH:mm-HH:mm,DAY-HH:mm-HH:mm
    Example: !setSchedule MON-19:00-21:00,09:00-11:00,TUE-19:00-21:00,THU-19:00-21:00

<b>!schedule:</b> Will Display the input user's schedule. if no user is input it will output your schedule.

	Usage: !schedule [userName] or !schedule
	Example: !schedule kimbalm2
           !schdeule

<b>!addTimes:</b> Add free times to DAY

	Usage: !addTimes DAY hh:mm-hh:mm,hh:mm-hhmm
	Example: !addTimes MON 19:00-21:00

<b>!removeTimes:</b> remove free times from DAY

	Usage: !removeTimes DAY hh:mm-hh:mm,hh:mm-hhmm
	Example: !removeTimes MON 19:00-21:00
  
  <b>!help:</b> Forgot how to use the bot? Ask it for help!

## Future Work
- I would like to improve the usability of the setSchedule and update commands to be nicer than having to enter a single long string as your schedule. 
  - Possible Solution: Google's [Calendar api](https://developers.google.com/calendar/quickstart/java) 
  - I might be able to move my userSchedule into google calendar but would like to save it for a future iteration of the bot while I focus on creating the original code base.




