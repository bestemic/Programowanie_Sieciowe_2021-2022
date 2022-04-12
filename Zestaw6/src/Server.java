import java.io.*;
import java.net.*;

// Serwer przyjmujący połączenia i odpalający obsługę klienta
public class Server {
    public static void main(String[] args) throws IOException {

        // Tworzenie gniazdka serwera
        ServerSocket serverSocket = new ServerSocket(2020);

        // Oczekiwanie na klientów
        while (true) {
            Socket socket = null;

            try {
                // Akceptacja połączenia z klientem
                socket = serverSocket.accept();

                // Pobranie deskryptorów
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());

                // Uruchomienie obsługi lienta w osobnym wątku
                Thread t = new ClientHandler(socket, input, output);
                t.start();

            } catch (Exception e) {
                if (socket != null) {
                    socket.close();
                }
                e.printStackTrace();
            }
        }
    }
}

// Obsługa klienta
class ClientHandler extends Thread {
    final DataInputStream input;
    final DataOutputStream output;
    final Socket socket;

    // Konstruktor
    public ClientHandler(Socket socket, DataInputStream input, DataOutputStream output) {
        this.socket = socket;
        this.input = input;
        this.output = output;
    }

    @Override
    public void run() {

        // Zmienne pomocnicze
        byte data;
        int maxLen = String.valueOf(Long.MAX_VALUE).length();
        byte[] number = new byte[maxLen];
        boolean isSpace = false;
        boolean isNumber = false;
        boolean isEnd = false;
        boolean isBeginZero = true;
        boolean isZero = true;
        boolean zeroCorrection = false;
        boolean error = false;
        boolean doConversion = false;
        boolean sendMessage = false;
        int numLen = 0;
        long sum = 0;
        long leftSpace = Long.MAX_VALUE;
        byte[] failMessage = {'E', 'R', 'R', 'O', 'R', '\r', '\n'};
        long time = 0;
        boolean isTimeout = false;

        // Odczyt danych od klienta
        while (true) {
            try {
                // Czekanie na pojawienie się danych
                while (input.available() < 1) {
                    // Uruchomienie obslugi timeoutu
                    if (time == 0) {
                        time = System.currentTimeMillis();
                    }

                    // Sprawdzenie czy nie wystąpił timeout
                    if (System.currentTimeMillis() - time >= 5000) {
                        isTimeout = true;
                    }

                    Thread.sleep(250);
                }

                // Obsługa timeoutu
                if (isTimeout) {
                    break;
                } else {
                    time = 0;
                }

                // Odczyt bajtu od klienta
                data = input.readByte();

                // Sprawdzenie czy wykryto koniec linii
                if (!isEnd) {

                    // Sprawdzenie czy nie ma błędu
                    if (!error) {
                        // Bajt cyfrą
                        if (data >= 48 && data <= 57) {
                            isNumber = true;
                            isSpace = false;

                            // Zero na początku liczby
                            if (data == 48 && isZero) {

                                // Pierwsze zero
                                if (isBeginZero) {
                                    isBeginZero = false;
                                    number[numLen] = data;
                                    numLen++;
                                }

                            } else {
                                isZero = false;

                                // Istniało zero na początku liczby
                                if (!isBeginZero && !zeroCorrection) {
                                    zeroCorrection = true;
                                    numLen--;
                                }

                                // Sprawdzenie czy cyfra nie za długa
                                if (numLen < maxLen) {
                                    number[numLen] = data;
                                    numLen++;
                                } else {
                                    error = true;
                                }
                            }
                        }

                        // Bajt spacją
                        if (data == ' ') {
                            // Sprawdzenie czy to kolejna spacja lub pierwsza w wiadomości
                            if (isNumber && !isSpace) {
                                isSpace = true;
                                doConversion = true;
                            } else {
                                error = true;
                            }
                        }

                        // Inny znak
                        if ((data < 48 || data > 57) && data != ' ' && data != '\r') {
                            error = true;
                        }
                    }

                    // Bajt \r
                    if (data == '\r') {
                        // Sprawdzenie czy nie ma spacji przed końcem, czy była liczba i czy nie ma powielonego znaku końca
                        if (!isSpace) {
                            doConversion = true;
                        } else {
                            error = true;
                        }

                        isEnd = true;
                    }

                } else {
                    // Bajt \n
                    if (data == '\n') {
                        isEnd = false;
                        sendMessage = true;
                    } else {
                        error = true;

                        // Obsługa kilku \r pod rząd
                        if (data != '\r') {
                            isEnd = false;
                        }
                    }
                }

                // Obsług konwersji
                if (doConversion) {

                    try {
                        // Konwersja
                        long sumElem = Long.parseLong(new String(number, 0, numLen));

                        // Sprawdzenie czy po dodaniu nie wystąpi przepełnienie
                        if (sumElem <= leftSpace) {
                            sum += sumElem;
                            leftSpace -= sumElem;
                        } else {
                            error = true;
                            isSpace = false;
                        }

                    } catch (NumberFormatException e) {
                        error = true;
                        isSpace = false;
                    }

                    numLen = 0;
                    doConversion = false;
                    isBeginZero = true;
                    isZero = true;
                }

                // Obsługa wysłania odpowiedzi
                if (sendMessage) {
                    // Odsyłanie wiadomości
                    if (!error) {
                        String value = Long.toString(sum);

                        byte[] succesMessage = new byte[value.length() + 2];
                        byte[] end = {'\r', '\n'};

                        System.arraycopy(value.getBytes(), 0, succesMessage, 0, value.length());
                        System.arraycopy(end, 0, succesMessage, value.length(), 2);

                        output.write(succesMessage);

                    } else {
                        output.write(failMessage);
                    }

                    isSpace = false;
                    isNumber = false;
                    isEnd = false;
                    isBeginZero = true;
                    isZero = true;
                    zeroCorrection = false;
                    error = false;
                    sendMessage = false;
                    sum = 0;
                    leftSpace = Long.MAX_VALUE;
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Zwolnienie zasobów
        try {
            this.input.close();
            this.output.close();
            this.socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
