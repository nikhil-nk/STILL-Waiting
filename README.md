## STILL-Waiting

This app is an SQLite, and SMS API based solution built for Android to manage queues at Hotels, Offices, Banks, etc, virtually any place where there is a long queue. 

This app basically empowers you to manage these long queues in a very organised and efficient manner. 

In the initial Layout of the App, We have three textboxes for getting information about a person while registering his/her name in the queue. 
* The first one is for the name.
* The second can be for "the number of capacity of the table to be reserved for this person in a hotel"
* The third one is for the phone number of the person.

After registering, the person's information is added in the SQLite databasse and his name and the second input "example, the seating capacity", are visible to us and are added in a time sorted list.

As soon as we see a vacancy for any person from the queue, all we need to do is just swipe off his/her name from the list and the app will automatically send a personalised sms to the person using an SMS api

An important thing to note is that since the data is stored in a database, the data is not lost even if the app is permanently closed or even if the phone is shut down. 

This allows you to manage the queue, even if it is on 24x7.

Here's a video explaining the functionality of the app:

[![STILL Waiting](https://img.youtube.com/vi/WiLAq2nQbDE/0.jpg)](https://youtu.be/WiLAq2nQbDE)
