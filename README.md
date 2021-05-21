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
