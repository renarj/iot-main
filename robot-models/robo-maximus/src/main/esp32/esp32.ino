
#include <ArduinoJson.h>

int LED_PIN = 13;
int SEND_PIN = 26;
int FEEDBACK_TIMEOUT = 500;

bool debugMode = false;
//{"command":"dynamixel","wait":"true","dxldata":"FFFFFD00650300010B9E"}

void printDebug(const char * message) {
  if(debugMode) {
    Serial.println(message);
  }
}

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
  int byteIndex = 0;

  printDebug("Entering hex to byte conversion");
  printDebug(("Lenght: " + String(strlen(hexString))).c_str());
  for (int charIndex = 0; charIndex < strlen(hexString); charIndex++)
  {
    printDebug(("Loop: " + String(charIndex)).c_str());
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
  printDebug("Exiting hex to byte conversion");
}

void setup() {
  pinMode(SEND_PIN, OUTPUT);
  pinMode(LED_PIN, OUTPUT);

  // sensors.setup();
  Serial1.setRxBufferSize(1024);
  Serial.setRxBufferSize(1024);
  Serial.setTxBufferSize(1024);
  Serial1.begin(57600);
  Serial.begin(57600);
}

void loop() {
  flushDynaBuffer();
  digitalWrite(LED_PIN, HIGH);
  String inputString = waitForCommand();
  bool listenFeedback = handleCommand(inputString);

  digitalWrite(LED_PIN, LOW);

  if(listenFeedback) {
    String feedback = receiveFeedback();
    char buf[11];
    sprintf (buf, "%04i", feedback.length()) ;
    Serial.print(buf);
    Serial.print(feedback);
    Serial.flush();
  } else {
    printDebug("Skipping waiting for feedback");
  }
}

void flushDynaBuffer() {
  while(Serial1.available()) {
    Serial1.read();
  }
}

bool handleCommand(String inputString) {
  if(inputString.length() > 0) {
    printDebug(("Received: " + inputString).c_str());

    DynamicJsonDocument doc(1024);
    deserializeJson(doc, inputString);

    String command = doc["command"];
    String wait = doc["wait"];

    printDebug(("We have a command: " + command).c_str());

    if(String("dynamixel").equalsIgnoreCase(command)) {
      String dxlData = doc["dxldata"];
      printDebug(("Sending DXL Data: " + dxlData).c_str());

      int nrBytes = dxlData.length() / 2;
      byte byteArray[nrBytes] = {0};
      hexCharacterStringToBytes(byteArray, dxlData.c_str());

      printDebug("Byte conversion completed");

      digitalWrite(SEND_PIN, HIGH);
      delay(5);
      Serial1.write(byteArray, nrBytes);
      Serial1.flush();
      digitalWrite(SEND_PIN, LOW);
      delay(5);

      printDebug("Command delivered");
    } else if (String("sensors").equalsIgnoreCase(command)) {
      // sensors.readSensors();

      String feedback = "{\"feedback\":{}";
      // feedback.concat(sensors.getJson());
      feedback.concat(",\"format\":\"json\"}");
      feedback.trim();
      Serial.println(feedback);

      return false;
    } else if(String("toggleDebug").equalsIgnoreCase(command)) {
      debugMode = !debugMode;

      printDebug("Debug mode enabled");
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
  feedback.concat("\",\"format\":\"hex\"");
  feedback.concat(",\"memory\":");
  feedback.concat(String(esp_get_free_heap_size()));
  feedback.concat("}");
  return feedback;
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
          delay(10);
        }
    }

    return inputString;
}
