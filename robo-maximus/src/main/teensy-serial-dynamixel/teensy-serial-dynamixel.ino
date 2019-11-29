
#include <ArduinoJson.h>
#include "Sensors.h"

/*
 * TEST Command: Turn on Torgue of servo 124
 * {"command":"dynamixel","dxldata":"FFFFFD007C070003400001015D8A"}
 * 
 * 
 * Torgue OFF
 * {"command":"dynamixel","dxldata":"FFFFFD007C070003400000015E0C"}
 */

int LED_PIN = 13;
int SEND_PIN = 2;
int FEEDBACK_TIMEOUT = 200;

ControllerSensors sensors = ControllerSensors();

byte nibble(char c)
{
  if (c >= '0' && c <= '9')
    return c - '0';

  if (c >= 'a' && c <= 'f')
    return c - 'a' + 10;

  if (c >= 'A' && c <= 'F')
    return c - 'A' + 10;

  return 0;  // Not a valid hexadecimal character
}

void hexCharacterStringToBytes(byte *byteArray, const char *hexString)
{
  bool oddLength = strlen(hexString) & 1;

  byte currentByte = 0;
  byte byteIndex = 0;

  for (byte charIndex = 0; charIndex < strlen(hexString); charIndex++)
  {
    bool oddCharIndex = charIndex & 1;

    if (oddLength)
    {
      // If the length is odd
      if (oddCharIndex)
      {
        // odd characters go in high nibble
        currentByte = nibble(hexString[charIndex]) << 4;
      }
      else
      {
        // Even characters go into low nibble
        currentByte |= nibble(hexString[charIndex]);
        byteArray[byteIndex++] = currentByte;
        currentByte = 0;
      }
    }
    else
    {
      // If the length is even
      if (!oddCharIndex)
      {
        // Odd characters go into the high nibble
        currentByte = nibble(hexString[charIndex]) << 4;
      }
      else
      {
        // Odd characters go into low nibble
        currentByte |= nibble(hexString[charIndex]);
        byteArray[byteIndex++] = currentByte;
        currentByte = 0;
      }
    }
  }
}

void setup() {
  pinMode(SEND_PIN, OUTPUT);
  pinMode(LED_PIN, OUTPUT);

  sensors.setup();
  
  Serial1.begin(57600);
  Serial.begin(57600);
}

void loop() {
  digitalWrite(LED_PIN, HIGH);  
  String inputString = waitForCommand();
  bool listenFeedback = handleCommand(inputString);

  digitalWrite(LED_PIN, LOW);
  
  if(listenFeedback) {
    String feedback = receiveFeedback();
    Serial.println(feedback);
  }
}

bool handleCommand(String inputString) {
  if(inputString.length() > 0) {
//    Serial.println("Received: " + inputString);
  
    DynamicJsonDocument doc(1024);
    deserializeJson(doc, inputString);
  
    String command = doc["command"];
    String wait = doc["wait"];
  
//    Serial.println("We have a command: " + command);
  
    if(String("dynamixel").equalsIgnoreCase(command)) {
      String dxlData = doc["dxldata"];
//      Serial.println("Sending DXL Data: " + dxlData);
  
      int nrBytes = dxlData.length() / 2;
      byte byteArray[nrBytes] = {0};
      hexCharacterStringToBytes(byteArray, dxlData.c_str());
  
      digitalWrite(SEND_PIN, HIGH);
      delay(5);
      Serial1.write(byteArray, nrBytes);
      Serial1.flush();
      digitalWrite(SEND_PIN, LOW);
      delay(5);
    } else if (String("sensors").equalsIgnoreCase(command)) {
      sensors.readSensors();
      
      String feedback = "{\"feedback\":\"{";
      feedback.concat(sensors.getJson());
      feedback.concat("}");
      feedback.trim();
      Serial.println(feedback);      

      return false;
    }

    return String("true").equalsIgnoreCase(wait);
  } else {
    return false;
  }
}

String receiveFeedback() {
  int start = millis();
  bool feedbackReceived = false;
  String feedback = "{\"feedback\":\"";
  
  while(!feedbackReceived) {
    int available = Serial1.available();   
    if(available) {
      while(Serial1.available()) {
        char tmp[8];
        int x = Serial1.read();
        sprintf(tmp, "%.2X ", x);
    
        feedback.concat(String(tmp));            
      }    
      feedbackReceived = true;      
    } else {
      int current = millis();
      if((current - start) > FEEDBACK_TIMEOUT) {
        feedbackReceived = true;
      } else {
        delay(10);
      }
    }
  }

  feedback.trim();
  return feedback.concat("\"}");
}

String receiveFeedbackOld() {
  delay(50);

  String feedback = "{\"feedback\":\"";

  int available = Serial1.available();
 
  for(int i=0; i<available; i++) {
    char tmp[8];
    int x = Serial1.read();
    sprintf(tmp, "%.2X ", x);
    
    feedback.concat(String(tmp));    
  }

  feedback.trim();

  return feedback.concat("\"}");
    
}

String waitForCommand() {
    String inputString = String("");
    bool complete = false;
    bool startedReceive = false;
    while (!complete) {
        if(Serial.available()) {
        
          char inchar = (char)Serial.read();         
          if (inchar == '}') {   
              inputString.concat(inchar);            
              complete = true;
              startedReceive = false;
          } else if(inchar == '{' || startedReceive) {
            startedReceive = true;
            inputString.concat(inchar);
          } 
        } else {
          delay(100);
        }
    }    

    return inputString;
}
