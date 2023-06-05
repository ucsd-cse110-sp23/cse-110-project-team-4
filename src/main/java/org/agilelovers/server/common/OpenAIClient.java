package org.agilelovers.server.common;

import io.github.jetkai.openai.api.CreateChatCompletion;
import io.github.jetkai.openai.api.CreateTranscription;
import io.github.jetkai.openai.api.data.audio.AudioData;
import io.github.jetkai.openai.api.data.completion.chat.ChatCompletionData;
import io.github.jetkai.openai.api.data.completion.chat.message.ChatCompletionMessageData;
import io.github.jetkai.openai.openai.OpenAI;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.Optional;

@NoArgsConstructor
public class OpenAIClient {

    @Value("${sayit.OPENAI_API_KEY}")
    private String API_KEY;

    public String getAnswer(String question) {
        ChatCompletionMessageData messageData = ChatCompletionMessageData.builder()
                .setRole("user")
                .setContent(question)
                .build();

        ChatCompletionData completionData = ChatCompletionData.builder()
                .setModel("gpt-3.5-turbo")
                .setMessages(Collections.singletonList(messageData))
                .build();

        OpenAI chatgpt = OpenAI.builder()
                .setApiKey(this.API_KEY)
                .createChatCompletion(completionData)
                .build()
                .sendRequest();

        CreateChatCompletion createChatCompletion = chatgpt.getChatCompletion();

        return createChatCompletion.asText();
    }

    public String getTranscription(MultipartFile file) {
        File audioFile = new File("audio.wav");
        try (FileOutputStream outputStream = new FileOutputStream(audioFile)) {
            outputStream.write(file.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        OpenAI whisper = OpenAI.builder()
                .setApiKey(this.API_KEY)
                .createTranscription(AudioData.create(audioFile.toPath()))
                .build()
                .sendRequest();

        Optional<CreateTranscription> createTranscription = whisper.transcription();

        audioFile.delete();

        return createTranscription.map(CreateTranscription::asText).orElse(null);
    }
}