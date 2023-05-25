package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02.Constants;
import ro.pub.cs.systems.eim.practicaltest02.Utilities;

public class ClientThread extends Thread{
    String clientAddress;
    int clientPort;
    String city;

    TextView weatherForecastTextView;

    private Socket socket;
    public ClientThread(String clientAddress, int clientPort, String city, TextView weatherForecastTextView) {
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
        this.city = city;

        this.weatherForecastTextView = weatherForecastTextView;
    }

    @Override
    public void run(){
        try {
            // tries to establish a socket connection to the server
            socket = new Socket(clientAddress, clientPort);

            // gets the reader and writer for the socket
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            // sends the city and information type to the server
            printWriter.println(city);
            printWriter.flush();
            String weatherInformation;

            // reads the weather information from the server
            while ((weatherInformation = bufferedReader.readLine()) != null) {
                final String finalizedWeateherInformation = weatherInformation;

                // updates the UI with the weather information. This is done using postt() method to ensure it is executed on UI thread
                weatherForecastTextView.post(() -> weatherForecastTextView.setText(finalizedWeateherInformation));
            }
        } // if an exception occurs, it is logged
        catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    // closes the socket regardless of errors or not
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}
