package full_beatbox;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class BeatBox {
    JPanel mainPanel;
    ArrayList<JCheckBox> checkBoxList;
    Sequencer sequencer;
    Sequence sequence;
    Track track;
    JFrame theFrame;

    String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat",
            "Acoustic Snare", "Crash Cymbal", "Hand Clap",
            "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga",
            "Cowbell", "Vibraslap", "Low-mid Tom", "High Agogo", "Open Hi Conga"};
    int[] instruments = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};

    public static void main(String[] args) {
        new BeatBox().buildGUI();
    }

    public void buildGUI() {
        theFrame = new JFrame("Cyber BeatBox");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel backgroud = new JPanel(layout);
        //Пустая граница позволяет создать поля между краями панели размещения компонентов
        backgroud.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        checkBoxList = new ArrayList<>();
        Box buttonBox = new Box(BoxLayout.Y_AXIS);

        JButton start = new JButton("Start");
        start.addActionListener(new MyStartListener());
        buttonBox.add(start);

        JButton stop = new JButton("Stop");
        stop.addActionListener(new MyStopListener());
        buttonBox.add(stop);

        JButton upTempo = new JButton("Tempo Up");
        upTempo.addActionListener(new MyUpTempoListener());
        buttonBox.add(upTempo);

        JButton downTempo = new JButton("Tempo Down");
        downTempo.addActionListener(new MyDownTempoListener());
        buttonBox.add(downTempo);

        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for (int i = 0; i < 16; i++) {
            nameBox.add(new Label(instrumentNames[i]));
        }

        backgroud.add(BorderLayout.EAST, buttonBox);
        backgroud.add(BorderLayout.WEST, nameBox);

        theFrame.getContentPane().add(backgroud);

        GridLayout grid = new GridLayout(16, 16);
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel = new JPanel(grid);
        backgroud.add(BorderLayout.CENTER, mainPanel);

        /*
        Создаем флажки, присваеиваем им значения false(что бы они не были установлены),
        а затем добавляем их в массив и на панель.
         */
        for (int i = 0; i < 256; i++) {
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkBoxList.add(c);
            mainPanel.add(c);
        }

        setUpMidi();

        theFrame.setBounds(50, 50, 300, 300);
        theFrame.pack();
        theFrame.setVisible(true);
    }

    /*
    Обычный Миди-код для получения синтезатора, секвенсора и дорожки.
     */
    public void setUpMidi() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buildTrackAndStart() {
        //Создаем массив из 16 элементов, что бы хранить значения для каждого инструмента, на все 16 тактов
        int[] trackList = null;
        //Избавляемся от старой дорожки и создаем новую
        sequence.deleteTrack(track);
        track = sequence.createTrack();

        //Делаем это для каждого из 16 рядов(т.е. для Bass, Congo и т.д.
        for (int i = 0; i < 16; i++) {
            trackList = new int[16];
            //Задаем клавишу, которая предсталяет инструмент. Массив содержит Миди-числа для каждого инструмента
            int key = instruments[i];

            for (int j = 0; j < 16; j++) {
                JCheckBox jc = (JCheckBox) checkBoxList.get(j + (16 * i));
                //Установлен ли флажок на этом такте?
                //Если да, то помещаем значения клавиш в текущую ячейку массива(ячейку, которая представляет такт)
                //Если нет, то инструмент не должен играть в этом такте, поэтому присваиваем ему 0
                if (jc.isSelected()) {
                    trackList[j] = key;
                } else {
                    trackList[j] = 0;
                }
            }
            //Для этого инструмента и для всех 16 тактов создаем события и добавляем их на дорожку
            makeTracks(trackList);
            track.add(makeEvent(176, 9, 1, 0, 15));
        }
        /*
        Мы всегда должны ть уверенны, что события на такте 16 существует(они идут от 0 до 15).
        Иначе BeatBox может не пройти все 16 тактов, перед тем как заново начнет последовательность
         */
        track.add(makeEvent(192, 9, 1, 0, 15));
        try {
            sequencer.setSequence((sequence));
            //ЛООП позволяет задать количество повторений цикла или, как в этом случае, непрерывный цикл
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MyStartListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            buildTrackAndStart();
        }
    }

    public class MyStopListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            sequencer.stop();
        }
    }

    public class MyUpTempoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            float tempoFactor = sequencer.getTempoFactor();
            //Коэф. темпа определяет темп синтазатора.
            // По умолчанию он 1, поэтому цельком кнопки мышы можно изменить его на +-0.3
            sequencer.setTempoFactor((float) (tempoFactor * 1.03));
        }
    }

    public class MyDownTempoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * .97));
        }
    }

    /*
    Метод создает события для одного инструмента за каждый проход цикла для всех 16 тактов.
    Можно получить int[] для Bass drum, и каждый элемент массива будет содержать либо клавишу этого инструмента, либо 0
    Если это ноль, то инструмент не должен играть на текущем такте.
    Иначе нужно создать событие и добавить его в дорожку.
     */
    public void makeTracks(int[] list) {
        for (int i = 0; i < 16; i++) {
            int key = list[i];

            if (key != 0) {
                //Создаем события включения и выключения и добавляем их в дорожку
                track.add(makeEvent(144, 9, key, 100, i));
                track.add(makeEvent(128, 9, key, 100, i + 1));
            }
        }
    }

    public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) {
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
