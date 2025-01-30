package full_beatbox;

import javax.sound.midi.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class BeatBoxFinal {
    private JFrame theFrame;
    private JPanel mainPanel;
    private JList incomingList;
    private JTextField userMessage;
    private ArrayList<JCheckBox> checkBoxList;
    private int nextNum;
    Vector<String> listVector = new Vector<String>();
    private String userName;
    ObjectOutputStream out;
    ObjectInputStream in;
    HashMap<String, boolean[]> otherSeqsMap = new HashMap<>();

    private Sequencer sequencer;
    private Sequence sequence;
    private Sequence mySequence = null;
    private Track track;
    private final File FILE_NAME = new File("CheckBox.ser");

    String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat",
            "Acoustic Snare", "Crash Cymbal", "Hand Clap",
            "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga",
            "Cowbell", "Vibraslap", "Low-mid Tom", "High Agogo", "Open Hi Conga"};
    int[] instruments = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};

    public static void main(String[] args) {
        new BeatBoxFinal().startUp("Alex");
    }

    public void startUp(String name) {
        userName = name;
        //Открываем соеднинение с сервером
        try {
            Socket sock = new Socket("127.0.0.1", 4242);
            out = new ObjectOutputStream(sock.getOutputStream());
            in = new ObjectInputStream(sock.getInputStream());
            Thread remote = new Thread(new RemoteReader());
            remote.start();
        } catch (Exception e) {
            System.out.println("couldn`n connect - you`ll have to play alone.");
        }

        setUpMidi();
        buildGUI();
    }

    public void buildGUI() {
        theFrame = new JFrame("Cyber BeatBox");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel backgroud = new JPanel(layout);
        //Пустая граница позволяет создать поля между краями панели размещения компонентов
        backgroud.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        checkBoxList = new ArrayList<JCheckBox>();

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

        JButton serializelt = new JButton("Serializelt");
        serializelt.addActionListener(new MySendListener());
        buttonBox.add(serializelt);

        JButton restore = new JButton("Restore");
        restore.addActionListener(new MyReadInListener());
        buttonBox.add(restore);

        JButton sendIt = new JButton("SendIt");
        sendIt.addActionListener(new MySendItListener());
        buttonBox.add(sendIt);

        userMessage = new JTextField();
        buttonBox.add(userMessage);

        incomingList = new JList<>();
        incomingList.addListSelectionListener(new MyListSelectionListener());
        incomingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane theList = new JScrollPane(incomingList);
        buttonBox.add(theList);
        incomingList.setListData(listVector);

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
        //Здесь будут храниться инструменты для каждого трека
        ArrayList<Integer> trackList = null;
        //Избавляемся от старой дорожки и создаем новую
        sequence.deleteTrack(track);
        track = sequence.createTrack();

        //Делаем это для каждого из 16 рядов(т.е. для Bass, Congo и т.д.
        for (int i = 0; i < 16; i++) {
            trackList = new ArrayList<Integer>();

            for (int j = 0; j < 16; j++) {
                JCheckBox jc = (JCheckBox) checkBoxList.get(j + (16 * i));
                //Установлен ли флажок на этом такте?
                //Если да, то помещаем значения клавиш в текущую ячейку массива(ячейку, которая представляет такт)
                //Если нет, то инструмент не должен играть в этом такте, поэтому присваиваем ему 0
                if (jc.isSelected()) {
                    int key = instruments[i];
                    trackList.add(new Integer(key));
                } else {
                    trackList.add(null);
                }
            }
            //Для этого инструмента и для всех 16 тактов создаем события и добавляем их на дорожку
            makeTracks(trackList);
        }
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
    public void makeTracks(ArrayList list) {
        Iterator it = list.iterator();
        for (int i = 0; i < 16; i++) {
            Integer num = (Integer) it.next();

            if (num != null) {
                //Создаем события включения и выключения и добавляем их в дорожку
                int numKey = num.intValue();
                track.add(makeEvent(144, 9, numKey, 100, i));
                track.add(makeEvent(128, 9, numKey, 100, i + 1));
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

    public class MySendListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean[] checkboxState = new boolean[256];

            for (int i = 0; i < 256; i++) {

                JCheckBox check = (JCheckBox) checkBoxList.get(i);
                if (check.isSelected()) {
                    checkboxState[i] = true;
                }
            }
            try {
                FileOutputStream fileStream = new FileOutputStream(FILE_NAME);
                ObjectOutputStream os = new ObjectOutputStream(fileStream);
                os.writeObject(checkboxState);
                System.out.println("Записалось");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public class MySendItListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent a) {
            //Создаем массив, в котором будут храниться только состояния флажков
            boolean[] checkboxState = new boolean[256];

            for (int i = 0; i < 256; i++) {

                JCheckBox check = (JCheckBox) checkBoxList.get(i);
                if (check.isSelected()) {
                    checkboxState[i] = true;
                }
            }
            String messageToSend = null;
            try {
                out.writeObject(userName + nextNum++ + ": " + userMessage.getText());
                out.writeObject(checkboxState);
            } catch (Exception exception) {
                System.out.println("Sorry dude. Could not send it to the server.");
            }
            userMessage.setText("");
        }
    }

    public class MyListSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                String selected = (String) incomingList.getSelectedValue();
                if (selected != null) {
                    //Переходим к отображению и изменяем последовательность
                    boolean[] selectedState = (boolean[]) otherSeqsMap.get(selected);
                    changeSequence(selectedState);
                    sequencer.stop();
                    buildTrackAndStart();
                }
            }
        }
    }

    public class RemoteReader implements Runnable {
        boolean[] checkboxState = null;
        String nameToShow = null;
        Object obj = null;

        public void run() {
            try {
                while ((obj = in.readObject()) != null) {
                    System.out.println("got an object from server");
                    System.out.println(obj.getClass());
                    String nameToShow = (String) obj;
                    checkboxState = (boolean[]) in.readObject();
                    otherSeqsMap.put(nameToShow, checkboxState);
                    listVector.add(nameToShow);
                    incomingList.setListData(listVector);
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }

    public class MyReadInListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean[] checkboxState = null;
            try {
                ObjectInputStream is = new ObjectInputStream(new FileInputStream(FILE_NAME));
                checkboxState = (boolean[]) is.readObject();
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            for (int i = 0; i < 256; i++) {
                JCheckBox check = (JCheckBox) checkBoxList.get(i);
                if (checkboxState[i]) {
                    check.setSelected(true);
                } else {
                    check.setSelected(false);
                }
            }

            System.out.println("Распаковалось");
            sequencer.stop();
            buildTrackAndStart();
        }
    }

    public class MyPlayMineListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (mySequence != null) {
                //Восстанавливаем до оригинальной последовательности
                sequence = mySequence;
            }
        }
    }

    public void changeSequence(boolean[] checkboxState) {
        for (int i = 0; i < 256; i++) {
            JCheckBox check = (JCheckBox) checkBoxList.get(i);
            if (checkboxState[i]) {
                check.setSelected(true);
            } else {
                check.setSelected(false);
            }
        }
    }
}
