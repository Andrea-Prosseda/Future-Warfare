

#Future Warfare

The idea of our project is the realization of the classic Laser Game with the support and integration of Android application.
Future Warfare is a game like paintball or airsoft without pain due to his laser nature to simulate the tagging / shooting of other players or targets.

Our concept is born with the aim to involve people in this kind of game without the need to go in an apposite Laser game center. With the requirements we’ll see later, every one can finally gets enjoy playing in every moment everywhere.

Thanks to HC-06 module bluetooth, we have the possibilty to always check our position in the map, steadily control our munitions and life, without forgetting enemies in the match. Moreover, the game provides friendly and deathmatch modality: in this way, fun/enjoy is ensured! 



</br><p align="center"><b><a href="http://www.modernwarfareapp.altervista.org/index.html">Visit our Web Site</a></b></p></br>
</br>
You can find the presentation of the project here:
http://www.slideshare.net/AndreaProsseda/future-warfare-64040999

And a Demo here:

# Used Tecnologies

<b>Android Side:</b>

- Android Studio

- Google Maps APIs: https://developers.google.com/maps/documentation/android-api/

- Hosting Database "Altervista.org": http://it.altervista.org

- Bluetooth Connection [Module HC-06]: http://www.amazon.it/dp/B0113MUGW0

<b>Arduino Side:</b>

- Arduino IDE

- Fritzing: http://fritzing.org/home/

- IRremote library: https://github.com/z3t0/Arduino-IRremote

# Architecture 

As we already discussed, there are two main connections:</br>
-Android application and Arduino side, which dialogues through Bluetooth</br>
-Android application and backend side, which dialogues through MySQL DataBase</br>

<p align="center"><img src="http://modernwarfareapp.altervista.org/images/Architecture.png" width="500" heigth="500"/></p>

#Backend
Here an Er Scheme of our DataBase:
<p align="center"><img src="http://modernwarfareapp.altervista.org/images/ErScheme.png" width="500" heigth="500"/> </p></br>

And here the Use Case Diagram:
<p align="center"><img src="http://modernwarfareapp.altervista.org/images/UseCaseDiagram.png" width="500" heigth="500"/> </p></br>

The server hosting our DB and WebSite is Altervista:
Altervista is an italian web platform founded by a Turin polytechnic student in 2000. It provides the possibility to create a web site with PHP, database SQL and FTP access.
</br></br>
# HowTo </br>

<b>Arduino Side:</b>
</br>
What we need:</br>
</br>
<b>Environment Creation</b></br>
- 1 Arduino Uno</br>
- 1 BreadBoard</br>
- 2 Colored LEDs</br>
- 2 Resistors for LEDs 220 Ohm</br>
- 1 Bluetooth Module HC-06</br>
- 1 Button</br>
- 1 Resistors for Button 10 KOhm</br>
</br>
<b>Core Gun Parts</b></br>
- Receiver: The receiver is a standard IR or Laser receiver module. We use a TSOP38238. It has 3 pins and it use a 220 Ohm Resistor. So the gun knows when it has been shot. The output pin of the receiver drops to a low voltage when a signal is received.</br>
- Transmitter: This is the most expensive part of the project. We need to use a diode laser addressed into a specific lens due to increase the radius of the pointlight. In this way is easier to hit the enemies receiver on their guns. </br>
<b>N.B.</b> If you have some old toy gun as Commodore64 or PS1 guns, the easiest way is to disassemble it and, instead of laser, you have to use a infrared LED because you already have the structure where integrate it (The guns abovementioned are already improved for this kind of infrared trasmission thanks to optical structure that hosts the lens).</br>
<p align="center"><img src="http://modernwarfareapp.altervista.org/images/Lens.png" width="500" heigth="500"/></p>
</br>If you want and if you have it, you can fill the whole Arduino structure in the gun (or if you prefer just receiver and trasmitter) in such a way to use apposite lens of the abovementioned gun. 
</br></br></br>
<b> OSS. </b></br>
The environment creation abovementioned is perfectly suitable for the both solution presented.
</br></br></br>

1) Configure Arduino according to the imagine below

<img src="http://modernwarfareapp.altervista.org/images/Fritzing2.png" width="350" heigth="350"/>	

2) Import Arduino code that you can find in the folder Arduino Code -> Future Warfare -> Future Warfare.ino

3) Download and Install in your Arduino IDE the library "IRremote" that you can find in the folder Arduino Code -> libraries

4) Load code in your Arduino

Lighting and sounding scenes advise you of the correctness operation.
After registration, login and creation of the match with the modality set, you can finally begin to game.
The receiver is steadily monitoring for an incoming signal: when an enemy will hit it light and sound will notificate it.
You can continue to play just if your life is greater than three (in deathMatch modality). 
In friendly game you can continue to play until the timer set in Android app is over.


<b>- Android Side</b>

1) Install .apk file of our application called Future Warfare on your Android Smartphone

2) Turn on GPS and Bluetooth

3) Run the App "Future Warfare"

4) Pair the Smartphone with Bluetooth Module HC-06 

For more information of Android Side you can check the web site, presentation or demo link.

Enjoy ;) 


# Team

<img src="http://modernwarfareapp.altervista.org/images/Andrea2.png" width="100" heigth="100"/>  

<b>Andrea Prosseda</b>

LinkedIn Page: https://www.linkedin.com/in/andrea-prosseda-2b8651116?trk=hp-identity-name

Email: andreaprosseda@gmail.com

<img src="http://modernwarfareapp.altervista.org/images/Gianluca2.png" width="100" heigth="100"/>  

<b>Gianluca Leo</b>

LinkedIn Page: https://www.linkedin.com/in/gianluca-leo-724032116?trk=hp-identity-name

Email: gianluca.leo.19@gmail.com

<img src="http://modernwarfareapp.altervista.org/images/Luca2.png" width="100" heigth="100"/>  

<b>Luca Mazzotti</b>

LinkedIn Page: https://www.linkedin.com/in/luca-mazzotti-532037116?trk=hp-identity-name

Email: luca_mazzotti@hotmail.it

</br></br>
<b> University of Rome "La Sapienza" </b> 

Pervasive Systems page: http://ichatz.me/index.php/Site/PervasiveSystems2016

Students at La Sapienza - University of Rome http://www.uniroma1.it

Master of Science in Engineering in Computer Science http://cclii.dis.uniroma1.it/?q=en/msecs

Department of DIAG http://www.diag.uniroma1.it# Future-Warfare
# Future-Warfare
