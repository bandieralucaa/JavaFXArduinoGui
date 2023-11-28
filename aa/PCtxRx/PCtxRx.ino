
//
//******* Programma lettura dati su porta seriale 
//
int  ledPin8 = 8;
int  ledPin7 = 7;
int  ledPin6 = 6;
int numeroVolte, intervallo;
bool p8, p7, p6, cls;
unsigned long tempo = 0;
unsigned long tver = 0;
String valori[3];
String s;
String idsk;

void setup() {

  Serial.begin(9600);
  pinMode(ledPin8, OUTPUT);
  pinMode(ledPin7, OUTPUT);
  pinMode(ledPin6, OUTPUT);
  idsk = "ARDU328PU";     // Stringa di riconoscimento

}

//
// Funzione per eseguire il ciclo on/off su pin 6
//
void eseguiCiclo(int num, int interv) {

  for (int j = 1; j < num; j++) {

    digitalWrite(ledPin6, HIGH);
    delay(interv);
    digitalWrite(ledPin6, LOW);
    delay(interv);
  }

}


void loop() {

  tempo = millis();

  // Ogni mezzo secondo controlla lo stato delle uscite
  //
  if (tempo > tver + 500) {

    Serial.println(idsk);
    switch (digitalRead(ledPin8)) {

      case HIGH:
        valori[0] = "led8 ON\n";
        break;

      case LOW:
        valori[0] = "led8 OFF\n";
        break;
    }

    switch (digitalRead(ledPin7)) {

      case HIGH:
        valori[1] = "led7 ON\n";
        break;

      case LOW:
        valori[1] = "led7 OFF\n";
        break;
    }


    switch (digitalRead(ledPin6)) {

      case HIGH:
        valori[2] = "led6 ON\n";
        break;

      case LOW:
        valori[2] = "led6 OFF\n";
        break;
    }

    for (int i = 0; i < 3; i++)
    {
      Serial.println(valori[i]); // Invia al Pc lo stato delle porte

    }

    tver = millis();
  }


  // Lettura dei dati dalla seriale
  //

  if (Serial.available() > 0)  {

    s = Serial.readString();

    if (s.equals("led8")) {
      p8 = not p8;
    }

    if (s.equals("led7")) {
      p7 = not p7;
    }

    if (s.equals("led6")) {
      p6 = not p6;

    }

    if (s.startsWith("@")) {
      int i1 = s.indexOf("c");
      intervallo =  s.substring(1, i1).toInt();
      numeroVolte = 1 + (s.substring(i1 + 1).toInt());
      cls = true;

    }

  }

  // Esegue i comandi rivevuti


  if (cls == true) {

    eseguiCiclo(numeroVolte, intervallo);
    cls = false;
  }

  if (p8 == true) {
    digitalWrite(ledPin8, HIGH);

  } else {
    digitalWrite(ledPin8, LOW);

  }

  if (p7 == true) {
    digitalWrite(ledPin7, HIGH);

  } else {
    digitalWrite(ledPin7, LOW);

  }

  if (p6 == true) {
    digitalWrite(ledPin6, HIGH);

  } else {
    digitalWrite(ledPin6, LOW);

  }

}
