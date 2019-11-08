#include "Sensors.h"

void ControllerSensors::setup() 
{  
  // Before initializing the IMU, there are a few settings
  // we may need to adjust. Use the settings struct to set
  // the device's communication mode and addresses:
  imu.settings.device.commInterface = IMU_MODE_I2C;
  imu.settings.device.mAddress = LSM9DS1_M;
  imu.settings.device.agAddress = LSM9DS1_AG;
  if (!imu.begin())
  {
    while (1) {
      Serial.println("IMU failed to start");
      delay(100);
    }
  }

  if (!ina260.begin()) {    
    while (1) {
      Serial.println("INA260 failed to start");
      delay(100);
    }
  }
}

void ControllerSensors::readSensors() {
    if ( imu.gyroAvailable() )
  {
    imu.readGyro();
  }
  if ( imu.accelAvailable() )
  {
    imu.readAccel();
  }
  if ( imu.magAvailable() )
  {
    imu.readMag();
  }

  calcAttitude();

  readINA260();
}

void ControllerSensors::readINA260() {
  current = ina260.readCurrent();
  voltage = ina260.readBusVoltage();
  power = ina260.readPower();
}

void ControllerSensors::printGyro()
{
  // Now we can use the gx, gy, and gz variables as we please.
  // Either print them as raw ADC values, or calculated in DPS.
  Serial.print("G: ");
#ifdef PRINT_CALCULATED
  // If you want to print calculated values, you can use the
  // calcGyro helper function to convert a raw ADC value to
  // DPS. Give the function the value that you want to convert.
  Serial.print(imu.calcGyro(imu.gx), 2);
  Serial.print(", ");
  Serial.print(imu.calcGyro(imu.gy), 2);
  Serial.print(", ");
  Serial.print(imu.calcGyro(imu.gz), 2);
  Serial.println(" deg/s");
#elif defined PRINT_RAW
  Serial.print(imu.gx);
  Serial.print(", ");
  Serial.print(imu.gy);
  Serial.print(", ");
  Serial.println(imu.gz);
#endif
}

void ControllerSensors::printAccel()
{  
  // Now we can use the ax, ay, and az variables as we please.
  // Either print them as raw ADC values, or calculated in g's.
  Serial.print("A: ");
#ifdef PRINT_CALCULATED
  // If you want to print calculated values, you can use the
  // calcAccel helper function to convert a raw ADC value to
  // g's. Give the function the value that you want to convert.
  Serial.print(imu.calcAccel(imu.ax), 2);
  Serial.print(", ");
  Serial.print(imu.calcAccel(imu.ay), 2);
  Serial.print(", ");
  Serial.print(imu.calcAccel(imu.az), 2);
  Serial.println(" g");
#elif defined PRINT_RAW 
  Serial.print(imu.ax);
  Serial.print(", ");
  Serial.print(imu.ay);
  Serial.print(", ");
  Serial.println(imu.az);
#endif

}

void ControllerSensors::printMag()
{  
  // Now we can use the mx, my, and mz variables as we please.
  // Either print them as raw ADC values, or calculated in Gauss.
  Serial.print("M: ");
#ifdef PRINT_CALCULATED
  // If you want to print calculated values, you can use the
  // calcMag helper function to convert a raw ADC value to
  // Gauss. Give the function the value that you want to convert.
  Serial.print(imu.calcMag(imu.mx), 2);
  Serial.print(", ");
  Serial.print(imu.calcMag(imu.my), 2);
  Serial.print(", ");
  Serial.print(imu.calcMag(imu.mz), 2);
  Serial.println(" gauss");
#elif defined PRINT_RAW
  Serial.print(imu.mx);
  Serial.print(", ");
  Serial.print(imu.my);
  Serial.print(", ");
  Serial.println(imu.mz);
#endif
}

// Calculate pitch, roll, and heading.
// Pitch/roll calculations take from this app note:
// http://cache.freescale.com/files/sensors/doc/app_note/AN3461.pdf?fpsp=1
// Heading calculations taken from this app note:
// http://www51.honeywell.com/aero/common/documents/myaerospacecatalog-documents/Defense_Brochures-documents/Magnetic__Literature_Application_notes-documents/AN203_Compass_Heading_Using_Magnetometers.pdf
void ControllerSensors::calcAttitude()
{
  float ax = imu.ax;
  float ay = imu.ay;
  float az = imu.az;
  float my = -imu.my;
  float mx = -imu.mx;
  float mz = imu.mz;
//
//  printGyro();
//  printAccel();
//  printMag();
  
  float troll = atan2(ay, az);
  float tpitch = atan2(-ax, sqrt(ay * ay + az * az));
  
  float theading;
  if (mx == 0)
    theading = (my < 0) ? PI : 0;
  else
    theading = atan2(my, mx);
    
  theading -= DECLINATION * PI / 180;
  
  if (theading > PI) theading -= (2 * PI);
  else if (theading < -PI) theading += (2 * PI);
  
  // Convert everything from radians to degrees:
  heading = theading * (180.0 / PI);
  pitch = tpitch * (180.0 / PI);
  roll  = troll * (180.0 / PI);
}

String ControllerSensors::getJson() {
  String json = String("\"sensors\":{\"ina260\":{\"current\":");
  json.concat(getCurrent());
  json.concat(",\"voltage\":");
  json.concat(getVoltage());
  json.concat(",\"power\":");
  json.concat(getPower());
  json.concat("},\"LSM9DS1\":{\"roll\":");
  json.concat(getRoll());
  json.concat(".\"pitch\":");
  json.concat(getPitch());
  json.concat(",\"heading\":");
  json.concat(getHeading());
  json.concat("}}}");

  return json;
}

float ControllerSensors::getVoltage() {
  return voltage;
}

float ControllerSensors::getCurrent() {
  return current;
}

float ControllerSensors::getPower() {
  return power;
}

float ControllerSensors::getHeading() {
  return heading;
}

float ControllerSensors::getPitch() {
  return pitch;
}

float ControllerSensors::getRoll() {
  return roll;
}
