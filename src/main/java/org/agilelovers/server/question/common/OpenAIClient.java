package org.agilelovers.server.question.common;

import io.github.jetkai.openai.api.CreateChatCompletion;
import io.github.jetkai.openai.api.CreateTranscription;
import io.github.jetkai.openai.api.data.audio.AudioData;
import io.github.jetkai.openai.api.data.completion.chat.ChatCompletionData;
import io.github.jetkai.openai.api.data.completion.chat.message.ChatCompletionMessageData;
import io.github.jetkai.openai.openai.OpenAI;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.Optional;

public class OpenAIClient {
    public static String getAnswer(String question) {
        ChatCompletionMessageData messageData = ChatCompletionMessageData.builder()
                .setRole("user")
                .setContent(question)
                .build();

        ChatCompletionData completionData = ChatCompletionData.builder()
                .setModel("gpt-3.5-turbo")
                .setMessages(Collections.singletonList(messageData))
                .build();

        OpenAI chatgpt = OpenAI.builder()
                .setApiKey("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")
                .createChatCompletion(completionData)
                .build()
                .sendRequest();

        CreateChatCompletion createChatCompletion = chatgpt.getChatCompletion();

        return createChatCompletion.asText();
    }

    public static String getTranscription(MultipartFile file) {
        File audioFile = new File("audio.wav");
        try (FileOutputStream outputStream = new FileOutputStream(audioFile)) {
            outputStream.write(file.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        OpenAI whisper = OpenAI.builder()
                .setApiKey("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")
                .createTranscription(AudioData.create(audioFile.toPath()))
                .build()
                .sendRequest();

        Optional<CreateTranscription> createTranscription = whisper.transcription();

        audioFile.delete();

        return createTranscription.map(CreateTranscription::asText).orElse(null);
    }
}
