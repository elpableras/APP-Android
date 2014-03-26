APP-Android
===========

Development of an App for Android, this app can serve as store notes, the functions are:

1. Activities
Various activities to exchange data between them, like:
 * Splash, is the initialization screen
 * Home, where is the principal screen with the options of the application
 * Date, return the date and time using a DatePicker and a TimePicker
 * ListNotes, activity where it is created an adapter to display the list of notes with the corresponding image. Also it has the option to remove the notes and the list will be updated immediately.

2. Management and Image Management
This option is handled in the Home activity, through access to the phone's camera and access to the image store on your phone, get the URL Path and this is then included in the listnote.

3. Access to data
Is possible used for access to information, a database to store the notes with their fields, for this, I use SQLite, which have all options enabled.

4. ARS (Action Recognize Speech)
The ARS is also included in the Home, is used to add through of the button, voice notes.

5. TTS (Text to Speech)
This option is in Home and in the listNotes, the first is used when the data are stored, is the confirmation by voice and the second, is used for informed that the user is inside in the listNotes activity.

6. The combination and management of various sensors
The camera sensor is used for take photographs and used it as data attached to the note. GPS is used for the location of the point of the mobile, if is disabled, is possible use the WiFi for obtaining triangulating of the position of the phone. This geolocation, is used to store the address, for example, the photo that you took, the user can know the position by latitude and longitude and saving as data the address of this position in the note created.

7. Packaging
The application is possible upload to market

8. Other things
 * I use the DialogFragment, to display a dialog to the activation of the GPS.
 * I use thread, to show for a while and then close an activity (Splash).
 * I use JavaBenas to encapsulate the data of the note and can be used with SQLite in the application.
 * Options menu with two items, one for exit of the application and the other has information about me.


