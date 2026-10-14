/*
  Leitor RFID (MFRC522) para ESP32 - College Squire
  ---------------------------------------------------

  O que este codigo faz:
    - Fica esperando um cartao/tag RFID ser aproximado do leitor MFRC522.
    - Ao ler um cartao com sucesso, escreve "Leitura bem sucedida" no
      Monitor Serial (e o UID do cartao, para conferencia).
    - Acende um LED por 5 segundos e depois apaga.

  Biblioteca necessaria (instalar pelo Gerenciador de Bibliotecas do
  Arduino IDE, Sketch > Incluir Biblioteca > Gerenciar Bibliotecas):
    - "MFRC522" de GithubCommunity (autor: miguelbalboa)

  Ligacao sugerida (ESP32 <-> MFRC522):
    MFRC522 SDA (SS)  -> ESP32 GPIO 21
    MFRC522 SCK       -> ESP32 GPIO 18
    MFRC522 MOSI      -> ESP32 GPIO 23
    MFRC522 MISO      -> ESP32 GPIO 19
    MFRC522 RST       -> ESP32 GPIO 22
    MFRC522 3.3V      -> ESP32 3.3V (NUNCA ligar o MFRC522 em 5V)
    MFRC522 GND       -> ESP32 GND

  LED:
    - Por padrao usa o GPIO 2 (LED embutido na maioria das placas ESP32
      DevKit). Se sua placa nao tiver LED nesse pino, ligue um LED
      externo (com um resistor de ~220 ohms em serie) no pino definido
      em LED_PIN abaixo e ajuste o numero conforme a ligacao.
*/

#include <SPI.h>
#include <MFRC522.h>

#define SS_PIN   21
#define RST_PIN  22
#define LED_PIN   2

#define TEMPO_LED_LIGADO_MS 5000

MFRC522 mfrc522(SS_PIN, RST_PIN);

void setup() {
  Serial.begin(115200);
  while (!Serial) {
    ; // aguarda o Monitor Serial abrir (necessario em algumas placas)
  }

  SPI.begin();
  mfrc522.PCD_Init();

  pinMode(LED_PIN, OUTPUT);
  digitalWrite(LED_PIN, LOW);

  Serial.println("Leitor RFID pronto. Aproxime um cartao...");
}

void loop() {
  // Sem cartao novo perto do leitor, nao ha nada a fazer neste ciclo.
  if (!mfrc522.PICC_IsNewCardPresent()) {
    return;
  }

  // Cartao detectado, mas so prosseguimos se conseguirmos ler o UID dele.
  if (!mfrc522.PICC_ReadCardSerial()) {
    return;
  }

  Serial.println("Leitura bem sucedida");

  Serial.print("UID do cartao:");
  for (byte i = 0; i < mfrc522.uid.size; i++) {
    Serial.print(mfrc522.uid.uidByte[i] < 0x10 ? " 0" : " ");
    Serial.print(mfrc522.uid.uidByte[i], HEX);
  }
  Serial.println();

  digitalWrite(LED_PIN, HIGH);
  delay(TEMPO_LED_LIGADO_MS);
  digitalWrite(LED_PIN, LOW);

  // Encerra a comunicacao com este cartao antes de voltar a procurar outro.
  mfrc522.PICC_HaltA();
  mfrc522.PCD_StopCrypto1();
}
