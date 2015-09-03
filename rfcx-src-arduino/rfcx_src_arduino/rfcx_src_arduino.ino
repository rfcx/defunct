
#include <DHT.h>
#include <ctype.h>

// Serial command character
char cmd;
String serial_out = "";
char str_buffer_float[5];

// assign pins
int pin_in_environment = 2; // DHT22 input
int pin_in_lipoly_charging = A6; // digital pin 4, battery charging indicator
int pin_in_lipoly_complete = A7; // digital pin 6, battery charge complete indicator
int pin_out_phone_charge = 11; // digital toggle for enabling/disabling charging of the cell phone
int pin_state_phone_charge = 0; // saves last known state of phone charge pin

// initialize temp/humidity (environment) sensor
#define DHTPIN pin_in_environment
#define DHTTYPE DHT22
DHT dht(DHTPIN, DHTTYPE);

void setup () {
  Serial1.begin(9600);
  dht.begin();

  // initialize output pins
  pinMode(pin_out_phone_charge, OUTPUT);
  digitalWrite(pin_out_phone_charge, LOW);
  
  // initialize input pins
  pinMode(pin_in_lipoly_charging, INPUT);
  pinMode(pin_in_lipoly_complete, INPUT);
  
  test_phone_charge();
}

void loop () {
  
  // wait 500 ms between checks for serial data.
  delay(500);
  
  // only execute when serial data is received
  while (Serial1.available()) {
    cmd = Serial1.read();
    
    if (cmd == 'a') {
      // GET -> battery charging status: charging/complete (0/1)
      String value_lipoly_charging = (String) digitalRead(pin_in_lipoly_charging);
      String value_lipoly_complete = (String) digitalRead(pin_in_lipoly_complete);
      serial_out += value_lipoly_charging + "/" + value_lipoly_complete;
    
    } else if (cmd == 'b') {
      // GET -> internal environment: (degrees/humidity)
      String value_humidity = (String) dtostrf(dht.readHumidity(), 0, 2, str_buffer_float);
      String value_temperature = (String) dtostrf(dht.readTemperature(), 0, 2, str_buffer_float);
      serial_out += value_humidity + "/" + value_temperature;
      
    } else if (cmd == 's') {
      // SET -> USB power -> ON
      serial_out += (String) pin_state_phone_charge + "/" + (String) set_phone_charge(true);
      
    } else if (cmd == 't') {
      // SET -> USB power -> OFF
      serial_out += (String) pin_state_phone_charge + "/" + (String) set_phone_charge(false);
      
    }
    
    if (serial_out != "") {
      Serial1.print("_");
      Serial1.print(serial_out);
      Serial1.print("^" + (String) cmd + "*");
      serial_out = "";
    }
  
  }
  
}

void test_phone_charge() {
  set_phone_charge(true);
  delay(1000);
  set_phone_charge(false);
}

int set_phone_charge(boolean on_off) {
  if (on_off) {
    digitalWrite(pin_out_phone_charge, HIGH);
    pin_state_phone_charge = 1;
  } else {
    digitalWrite(pin_out_phone_charge, LOW);
    pin_state_phone_charge = 0;
  }
  return pin_state_phone_charge;
}
