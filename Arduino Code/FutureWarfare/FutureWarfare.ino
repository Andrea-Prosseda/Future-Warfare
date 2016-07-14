#include <IRremote.h>          //Library for the IR Receiver
#include <SoftwareSerial.h>    //Library for theBluetooth

SoftwareSerial BT(10, 9);   
IRrecv irrecv(11);             // Instance of class IRrecv used to receive
decode_results results;        // Buffer containing received signal il segnale ricevuto


int first_led=5;               //Pin of first Led
int second_led=3;              //Pin of second led 
int piezo=8;                   //Pin of Piezo
int trigger=4;                 //Pin of trigger
int current_Ammo;              //CurrentAmmo of the player
int deadTime;                  //DeadTime of the player
char mode;                     //Used to deal with differents Android Input
bool deathMatch=false;         //Used to set deathMatch modality
bool friendly=false;           //Used to set friendly modality


void setup() {
    BT.begin(9600);            //Start Bluethoot
    Serial.begin(9600);        //Start Serial
    inizializeEnvironment();   //Funcion used to inizialize the whole Environment
    delay(200);
}


void loop() {
//***************************SETTING BLUETOOTH************************//
    if (BT.available()){                                          //Waiting for bluetooth connection
          
         mode=(BT.read());                                        //Bluetooth input
        
        switch(mode){
          case '1':  
              BT.println((String)current_Ammo);                  //Send current_Ammo to Android if Bluetooth input is 1
              break;
          case '2':
              BT.println((String)deadTime);                      //Send deadTime to Android if Bluetooth input is 2
              break;
           case '4':                                             //Set deathMatch modality on Arduino if Android sends '4'
              current_Ammo=100;
              deadTime=0; 
              deathMatch=true;
              friendly=false;
              break;
           case '5':                                            //Set friendly modality on Arduino if Android sends '4'
              current_Ammo=100;
              deadTime=0; 
              friendly=true;
              deathMatch=false;
              break;
           case  '6':                                           //Set recharge of supply if player is inRange() 
              current_Ammo=100;
        }   

   }
//**********************************************************************//




//**********************************GAME********************************//
    if (irrecv.decode(&results)) {                              //If signal is present on buffer, player has been hit
      deadTime++;                                               //deadTime is incremented
      hit();                                                    //Function hit is called to manage hit
    }
    irrecv.resume();                                            //Refresh buffer and start receiving the next value
    
    int shot= digitalRead(trigger);                             
    if((shot==HIGH) && (deadTime<3 || friendly )){              //If it is deathMatch modality, you cannot shot if you are dead
        if(current_Ammo>0){    
          shoot();                                              //Function shoot is called to manage shoot 
        }   
    }
    
delay(40);

}



void shoot(){                                                   
    current_Ammo--;                                             //Decrease current_Ammo
    tone(piezo,250);                                            //Start piezo 
    digitalWrite(second_led,HIGH);                              //Led high       
    delay(300);
    noTone(piezo);                                              //Stop piezo after 300 ms
    digitalWrite(second_led,LOW);                               //Led low
}


 void hit(){
    
      Serial.println(results.value, HEX);                       //Print on monitor the HEX signal value
      tone(piezo,2000,2000);                                    //Start piezo 
        
      for(int i=0;i<5;i++){                                     //Start animation of led
        digitalWrite(first_led,LOW); 
        delay(300);
        digitalWrite(first_led,HIGH);
        delay(300);
      }
      noTone(piezo);                                            //Stop piezo
      digitalWrite(first_led,LOW);                              //Stop animation
}   

void inizializeEnvironment(){
      irrecv.enableIRIn();                                      // Start the receiver
      current_Ammo=100;                                         // Set current_Ammo to 100
      deadTime=0;                                               // Set deadTime to 0
      tone(piezo,1000);                                         //Start piezo
      pinMode(first_led,OUTPUT);                                //Set the right pin
      pinMode(second_led,OUTPUT);                               //Set the right pin
      pinMode(trigger,INPUT);                                   //Set the right pin

      digitalWrite(first_led,HIGH);                             //Start animation
      digitalWrite(second_led,HIGH);
      delay(3000);
      noTone(piezo);
      
      for(int i=0; i<3; i++){
        digitalWrite(first_led,LOW);
        digitalWrite(second_led,LOW);
        delay(300);
        digitalWrite(first_led,HIGH);   
        digitalWrite(second_led,HIGH);
        delay(300);
      }
      digitalWrite(first_led,LOW);
      digitalWrite(second_led,LOW);                             //Stop Animation
}
//**********************************************************************//
