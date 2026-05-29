import java.io.File;
import java.io.IOException;

public final class SoundEffectPlayer {

    private static final String ALLAHU_AKBAR_SOUND = "assets/sound effect/Allahu Akbar Sound Effect.mp3";
    private static final String WMPLAYER_EXE = "C:\\Program Files\\Windows Media Player\\wmplayer.exe";

    private SoundEffectPlayer() {
    }

    public static void playAllahuAkbarSound() {
        File soundFile = new File(ALLAHU_AKBAR_SOUND);
        if (!soundFile.exists()) {
            throw new IllegalStateException("Sound file not found: " + ALLAHU_AKBAR_SOUND);
        }

        ProcessBuilder processBuilder = new ProcessBuilder(
                WMPLAYER_EXE,
                "/play",
                "/close",
                soundFile.getAbsolutePath());

        try {
            processBuilder.start();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to start sound playback.", e);
        }
    }
}