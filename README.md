# Rck-Brs-App[2.0 - New version] 
Current version of WatchApp. An application that can store movies, TV series, games and books from the famous "Rock and Boris" podcast and allows the application to choose which items are still being watched and are waiting for further recommendations. The new version is more stable in assumptions.

Now I have used the Firestore database as this database is better for mass data collection as many filters modify the returned content.

My idea was to use three tables:
- Elements - the main data core - each user joining the "society", the system copies the full database to his private profile.

- News - list of changes, when someone deletes a record (administrator only), this change is saved in this table, and then the user after a few days from this change (if not logged in before), his records are updated to the latest database state. The system knew which update list had to be run for each user as there is a new login and last login timestamp. This table is skipped when someone logs in because they have received updated data from the main 'Items' table.
Why is there not only one table? Because each user can make changes to their list by "checking" it or not and waiting for new things to look at.

- Users - user accounts registered with Google. Each user in this table has their own list of items to update - which they watched or not. Besides, it had its own preferences (via filters) which data it wanted to display on the home screen. The user can modify the filters using complex checkboxes, podcast authors - eg "Show me only those <b> VIDEO </b> that" podcasts "<b> recommend as good </b>" and so on - lots of possibilities.
Filters are saved once and return to the same state that we left them each time until we change them.

Technology:
<ul>
<li>Java</li>
<li>Firestore Database</li>
<li>MVVM</li>
<li>Android</li>

 ## Images: 
 
<p align="center">
<img src="https://i.ibb.co/g37rVgK/Zrzut-ekranu-2021-05-24-140734.png" alt="Zrzut-ekranu-2021-05-24-140734" border="0">
<img src="https://i.ibb.co/VqpJytX/Zrzut-ekranu-2021-05-24-141232.jpg" alt="Zrzut-ekranu-2021-05-24-141232" border="0">
<img src="https://i.ibb.co/KXBx55K/Zrzut-ekranu-2021-05-24-141259.jpg" alt="Zrzut-ekranu-2021-05-24-141259" border="0">
<img src="https://i.ibb.co/fGZQGCt/Zrzut-ekranu-2021-05-24-141327.jpg" alt="Zrzut-ekranu-2021-05-24-141327" border="0">
<img src="https://i.ibb.co/3d41NSv/Zrzut-ekranu-2021-05-24-141359.jpg" alt="Zrzut-ekranu-2021-05-24-141359" border="0">
<img src="https://i.ibb.co/J7NmF3c/Zrzut-ekranu-2021-05-24-141439.jpg" alt="Zrzut-ekranu-2021-05-24-141439" border="0">
<img src="https://i.ibb.co/WW7sBwL/Zrzut-ekranu-2021-05-24-141505.jpg" alt="Zrzut-ekranu-2021-05-24-141505" border="0"></p>
  
 ### Firestore Database - User Data: 
 
<p align="center">
<img src="https://i.ibb.co/tcvdCf8/Zrzut-ekranu-2021-05-24-141601.jpg" alt="Zrzut-ekranu-2021-05-24-141601" border="0"></p>
