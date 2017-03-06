# GoGetIt
The app is a shopping list. 
To use this app, user scan the products such as grocieries, medicine, cococola etc., after that the other person who registered 
with the app will receive the scanned products along with its name and image. The list will be updated dynamically.
So, no more typings or typos, just scan and get the shopping done ;).

The app internally triggers the NEC API, to scan the product, which then stores everything into firebase realtime database, 
and all the registered users gets notified whenever there is an update.

Steps to run:
- Install UserAppSample.apk
- Open the app in the android studio and install in the device.
- Click on the plus sign at the bottom, this launches NEC API to scan the product, go to the "res" folder and scan it,
it shall add it to the list.
