#define CMD_SHOOT 0x01

// digital pin 2 has a pushbutton attached to it. Give it a name:
int led = 13;
bool state = true;

// the setup routine runs once when you press reset:
void setup() {
  // initialize serial communication at 9600 bits per second:
  Serial.begin(115200);
  pinMode(led, OUTPUT);

}

// the loop routine runs over and over again forever:
void loop() {
  
  if(Serial.available() > 1){
    char c = Serial.read();
    char n = Serial.read();
    if(c == CMD_SHOOT && n == 0x0A){
      state = !state;
    }
  }
  
  digitalWrite(led, state);
}



