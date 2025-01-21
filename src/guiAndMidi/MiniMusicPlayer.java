package guiAndMidi;

import javax.sound.midi.*;

/**
 * Версия 1.
 * Здесь нет обработки события или графики, а есть лишь последовательность из 15 восходящих нот.
 * Цель данного кода - изучение работы метода makeEvent().
 * Этот код уменьшает код для следующих двух версий.
 */
public class MiniMusicPlayer {
    public static void main(String[] args) {
        try {
            Sequencer sequencer = MidiSystem.getSequencer();
            sequencer.open();

            Sequence seq = new Sequence(Sequence.PPQ, 4);
            Track track = seq.createTrack();

            for (int i = 5; i < 61; i += 4) {
                track.add(makeEvent(144, 1, i, 100, i));
                track.add(makeEvent(128, 1, i, 100, i + 2));
            }

            sequencer.setSequence(seq);
            sequencer.setTempoInBPM(220);
            sequencer.start();
        } catch (MidiUnavailableException | InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }

    public static MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) {
        MidiEvent event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(comd, chan, one, two);
            event = new MidiEvent(a, tick);
            return event;
        } catch (Exception e) {
            return event;
        }
    }
}
