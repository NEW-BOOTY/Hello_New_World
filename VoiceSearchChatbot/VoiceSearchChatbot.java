/** Copyright © 2024 Devin B. Royal. All Rights Reserved. */

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VoiceSearchChatbot {
    private static Map<String, String> responses = new HashMap<>();

    static {
        responses.put("hello", "Hello! How can I assist you today?");
        responses.put("help", "Sure, I'm here to help. What do you need assistance with?");
        responses.put("search", "You can use our search engine to find information. Just type your query.");
        // Add more responses as needed
    }

    public static void main(String[] args) {
        try {
            Terminal terminal = TerminalBuilder.builder().system(true).build();
            LineReader reader = LineReaderBuilder.builder().terminal(terminal).build();

            while (true) {
                String input = reader.readLine("Enter 'voice' for voice search or type your query (or 'exit' to quit): ");
                if (input.equalsIgnoreCase("exit")) {
                    break;
                }
                if (input.equalsIgnoreCase("voice")) {
                    String voiceInput = getVoiceInput();
                    terminal.writer().println("You said: " + voiceInput);
                    respond(voiceInput, terminal);
                } else {
                    respond(input, terminal);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getVoiceInput() throws IOException {
        try (TargetDataLine microphone = AudioSystem.getTargetDataLine(new AudioFormat(16000, 16, 1, true, false))) {
            microphone.open();
            microphone.start();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = microphone.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            ByteString audioBytes = ByteString.copyFrom(out.toByteArray());

            try (SpeechClient speechClient = SpeechClient.create()) {
                RecognitionConfig config = RecognitionConfig.newBuilder()
                        .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                        .setSampleRateHertz(16000)
                        .setLanguageCode("en-US")
                        .build();
                RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

                RecognizeResponse response = speechClient.recognize(config, audio);
                return response.getResultsList().get(0).getAlternativesList().get(0).getTranscript();
            }
        } catch (LineUnavailableException e) {
            throw new IOException("Microphone not available", e);
        }
    }

    private static void respond(String input, Terminal terminal) {
        String response = responses.getOrDefault(input.toLowerCase(), "I'm sorry, I don't understand that. Can you please rephrase?");
        terminal.writer().println("Bot: " + response);
    }
}