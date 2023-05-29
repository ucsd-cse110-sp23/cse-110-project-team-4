package org.agilelovers.server.transcribe;

import org.agilelovers.server.common.OpenAIClient;
import org.agilelovers.server.common.errors.UserNotFoundError;
import org.agilelovers.server.transcribe.errors.NoAudioError;
import org.agilelovers.server.user.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class TranscribeController {

    private final UserRepository users;
    private final OpenAIClient client;

    public TranscribeController(UserRepository users) {
        this.users = users;
        this.client = new OpenAIClient();
    }

    @PostMapping ("/api/transcribe/{uid}")
    public TranscribeData transcribe(@RequestParam("file")MultipartFile file, @PathVariable String uid) {
        if (!users.existsById(uid))
            throw new UserNotFoundError(uid);

        String result = this.client.getTranscription(file);

        if (result != null && !result.isEmpty())
            return TranscribeData.builder()
                    .transcribed(result)
                    .build();
        else
            throw new NoAudioError();
    }
}
