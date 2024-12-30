#ifndef __SENSORS_H__
#define __SENSORS_H__

#include <Wire.h>
#include <SPI.h>
#include <SparkFunLSM9DS1.h>
#include <Adafruit_INA260.h>
#include "Adafruit_MCP9808.h"


#define LSM9DS1_M 0x1E // Would be 0x1C if SDO_M is LOW
#define LSM9DS1_AG  0x6B // Would be 0x6A if SDO_AG is LOW

#define PRINT_CALCULATED
//#define PRINT_RAW
#define PRINT_SPEED 250 // 250 ms between prints

// Earth's magnetic field varies by location. Add or subtract 
// a declination to get a more accurate heading. Calculate 
// your's here:
// http://www.ngdc.noaa.gov/geomag-web/#declination
#define DECLINATION -1.38 // Declination (degrees) in Boulder, CO.


class ControllerSensors {
  LSM9DS1 imu;
  Adafruit_INA260 ina260;

  float roll = 0.0;
  float pitch = 0.0;
  float heading = 0.0;

  float voltage = 0.0;
  float current = 0.0;
  float power = 0.0;

  float tempC = 0.0;
  
  void calcAttitude();

  void readINA260();

  void printMag();
  void printAccel();
  void printGyro();
  
public:
  void setup();

  void readSensors();

  float getRoll();

  float getPitch();

  float getHeading();

  float getVoltage();

  float getCurrent();

  float getPower();

  String getJson();

};

#endif
