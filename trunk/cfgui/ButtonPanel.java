package cfgui;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.sound.sampled.Clip;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import cfgen.*;

import java.util.*;

public class ButtonPanel extends JPanel implements ActionListener {
    
    JButton playCF = new JButton("Play Cantus Firmus");
    JButton checkCF = new JButton("Analyze Cantus Firmus");
    JButton check = new JButton("Analyze Counterpoint");
    JButton compose = new JButton("Compose Counterpoint");
    JButton clearS = new JButton("Clear Solution");
    JButton playSol = new JButton("Play Counterpoint");
    JButton playBoth = new JButton("Play Both");
    JButton clearCF = new JButton("Clear Cantus Firmus");    
    
    String[] majorKeys = {"C# Major", "F# Major", "B Major", "E Major", "A Major", "D Major", "G Major", "C Major", 
                          "F Major", "Bb Major", "Eb Major", "Ab Major", "Db Major", "Gb Major", "Cb Major"};
    
    String[] minorKeys = {"A# Minor", "D# Minor", "G# Minor", "C# Minor", "F# Minor", "B Minor", "E Minor", "A Minor",
                          "D Minor", "G Minor", "C Minor", "F Minor", "Bb Minor", "Eb Minor", "Ab Minor"};
    
    JComboBox keyList = new JComboBox(majorKeys);
    JRadioButton major = new JRadioButton("Major");
    JRadioButton minor = new JRadioButton("Minor");
    ButtonGroup keyButtons = new ButtonGroup();
    
    StaffPanel cfPanel;
    StaffPanel solPanel;
    MessagePanel mess;
    
    Collection<String> solutions = null;
    Pitch[] solPitches;
    Pitch[] cfPitches;
    
    public ButtonPanel(StaffPanel cf, StaffPanel solution, MessagePanel messages) {
        cfPanel = cf;
        solPanel = solution;
        mess = messages;
        
        setBackground(Color.white);
        setLayout(null);
        
        clearCF.setBounds(25, 450, 200, 40);	
        checkCF.setBounds(25, 410, 200, 40);
        playCF.setBounds(25, 370, 200, 40);	

        compose.setBounds(25, 280, 200, 40);
        playBoth.setBounds(25, 240, 200, 40);	

        clearS.setBounds(25, 150, 200, 40);
        check.setBounds(25, 110, 200, 40);
        playSol.setBounds(25, 70, 200, 40);	
        
        
        keyList.setBounds(25, 200, 100, 40);
        major.setBounds(125, 200, 100, 20);
        minor.setBounds(125, 220, 100, 20);
        
        
        checkCF.addActionListener(this);
        check.addActionListener(this);
        compose.addActionListener(this);
        clearS.addActionListener(this);
        clearCF.addActionListener(this);
        playCF.addActionListener(this);
        playSol.addActionListener(this);
        playBoth.addActionListener(this);
        
        
        keyButtons.add(major);
        keyButtons.add(minor);
        major.setSelected(true);
        major.addActionListener(this);
        minor.addActionListener(this);
        keyList.addActionListener(this);
        
        keyList.setSelectedIndex(7);
        
        add(checkCF);
        add(check);
        add(compose);
        add(clearS);
        add(clearCF);
        add(playCF);
        add(playSol);
        add(playBoth);
        /*add(major);
          add(minor);
          add(keyList);*/
    }
    
    public Color getColor(Comment c) {
        switch (c.severity) {
        case ERROR:
            return Color.red;
        case WARNING:
            return Color.orange;
        case FYI:
            return Color.green;
        case LGTM:
            return Color.blue;
        default:
            return Color.black;
        }
    }
    
    public void highlightNotes(Comment c) {
        for(MyNote note : solPanel.notes) {
            note.highlighted = false;
        }
        for(MyNote note : cfPanel.notes) {
            note.highlighted = false;
        }
        for(int i = 0; i < c.notes.length; i++) {
            //	    System.out.println(c.notes[i]);
            if(solPitches != null) {
                for(int j = 0; j < solPitches.length; j++) {
                    //      System.out.println("   " + solPitches[j]);
                    if( c.notes[i] == solPitches[j]) {
                        solPanel.notes.get(j).highlighted = true;
                    }
                }
            }
            for(int j = 0; j < cfPitches.length; j++) {
                //  System.out.println("   " + cfPitches[j]);
                if( c.notes[i] == cfPitches[j]) {
                    cfPanel.notes.get(j).highlighted = true;
                }
            }
        }
        solPanel.repaint();
        cfPanel.repaint();
    }	
    
    public boolean checkCF() {
        boolean isGood = false;
        Key key = new Key(new Pitch('c'));
        cfPitches = new Pitch[cfPanel.notes.size()];
        cfPanel.setPitches();
        for(int i = 0; i < cfPitches.length; i++) {
            MyNote note = cfPanel.notes.get(i);
            cfPitches[i] = key.getPitch(note.pitchName(), note.octave);
            //System.out.println(note);
            //System.out.println(CantusFirmus[i]);
        }
        CantusFirmus CFtoCheck = new CantusFirmus(cfPitches, key);
        Collection<Comment> comments = CFtoCheck.check();
        if(comments.size() == 0) {
            comments.add(new Comment("No errors found!", Comment.Severity.LGTM));
            isGood = true;
        }
        for(MyNote note : cfPanel.notes) {
            note.setColor(Color.black);
            note.highlighted = false;
        }
        for(Comment c : comments) {
            for(int i = 0; i < c.notes.length; i++) {
                for(int j = 0; j < cfPitches.length; j++) {
                    if( c.notes[i] == cfPitches[j]) {
                        cfPanel.notes.get(j).setColor(getColor(c));
                    }
                }
            }
        }
        cfPanel.repaint();
        mess.removeAll();
        mess.setMessages(comments);
        return isGood;
    }
    
    public void playNote(int signature) throws LineUnavailableException {
      float samples_per_second = 44100;
      AudioFormat format = new AudioFormat(samples_per_second, 8, 1, true, true);
      DataLine.Info clipInfo = new DataLine.Info(Clip.class, format);
      Clip clip = (Clip) AudioSystem.getLine(clipInfo);

      byte[] data = new byte[(int)samples_per_second];
      for (int i = 0; i < data.length; i++) {
        byte amount = 0;

        amount = (byte) (Math.sin(((i * 2 * Math.PI) / samples_per_second) * 130.812783 * Math.pow(2, signature/12.0)) * 127.0);
        data[i] = amount;
      }
      clip.open(format, data, 0, data.length);

      // due to bug in Java Sound, explicitly exit the VM when
      // the sound has stopped.
      /*clip.addLineListener(new LineListener() {
        public void update(LineEvent event) {
          if (event.getType() == LineEvent.Type.STOP) {
            event.getLine().close();
            System.exit(0);
          }
        }
      });*/

      clip.start();
      try {
        Thread.sleep(1000);
      } catch (InterruptedException ex) {
        System.out.println("Thread got interrupted! Fuck you!");
        System.exit(0);
      }
    }
    
    public void playNote(int signature1, int signature2) throws LineUnavailableException {
      float samples_per_second = 44100;
      AudioFormat format = new AudioFormat(samples_per_second, 8, 1, true, true);
      DataLine.Info clipInfo = new DataLine.Info(Clip.class, format);
      Clip clip = (Clip) AudioSystem.getLine(clipInfo);

      byte[] data = new byte[(int)samples_per_second];
      for (int i = 0; i < data.length; i++) {
        byte amount = 0;

        amount = (byte) (Math.sin(((i * 2 * Math.PI) / samples_per_second) * 130.812783 * Math.pow(2, signature1/12.0)) * 63.0);
        data[i] += amount;
        amount = (byte) (Math.sin(((i * 2 * Math.PI) / samples_per_second) * 130.812783 * Math.pow(2, signature2/12.0)) * 63.0);
        data[i] += amount;
      }
      clip.open(format, data, 0, data.length);

      // due to bug in Java Sound, explicitly exit the VM when
      // the sound has stopped.
      /*clip.addLineListener(new LineListener() {
        public void update(LineEvent event) {
          if (event.getType() == LineEvent.Type.STOP) {
            event.getLine().close();
            System.exit(0);
          }
        }
      });*/

      clip.start();
      try {
        Thread.sleep(1000);
      } catch (InterruptedException ex) {
        System.out.println("Thread got interrupted! Fuck you!");
        System.exit(0);
      }
    }
    
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(playCF)){
          cfPanel.setPitches();
          Key key = new Key(new Pitch('c'));
          int[] signatures = new int[cfPanel.notes.size()];

          for(int i = 0; i < signatures.length; i++) {
            MyNote note = cfPanel.notes.get(i);
            signatures[i] = key.getPitch(note.pitchName(), note.octave).getValue();
          }
          
          for (int sig : signatures) {
            try {
              playNote(sig);
            } catch (LineUnavailableException ex) {
              System.out.println("Got an exception trying to play a note: " + ex);
            }
          }
        }
        if(e.getSource().equals(playSol)){
          solPanel.setPitches();
          Key key = new Key(new Pitch('c'));
          int[] signatures = new int[solPanel.notes.size()];

          for(int i = 0; i < signatures.length; i++) {
            MyNote note = solPanel.notes.get(i);
            signatures[i] = key.getPitch(note.pitchName(), note.octave).getValue();
          }
          
          for (int sig : signatures) {
            try {
              playNote(sig);
            } catch (LineUnavailableException ex) {
              System.out.println("Got an exception trying to play a note: " + ex);
            }
          }
        }
        if(e.getSource().equals(playBoth)){
          solPanel.setPitches();
          cfPanel.setPitches();
          Key key = new Key(new Pitch('c'));
          int[] solSignatures = new int[solPanel.notes.size()];
          int[] cfSignatures = new int[cfPanel.notes.size()];

          for(int i = 0; i < solSignatures.length; i++) { // adjust when lists could be of different lengths
            MyNote solNote = solPanel.notes.get(i);
            solSignatures[i] = key.getPitch(solNote.pitchName(), solNote.octave).getValue();
            MyNote cfNote = cfPanel.notes.get(i);
            cfSignatures[i] = key.getPitch(cfNote.pitchName(), cfNote.octave).getValue();
          }
          
          for (int i = 0; i < solSignatures.length; i++) {
            int solSig = solSignatures[i];
            int cfSig = cfSignatures[i];
            try {
              playNote(cfSig, solSig);
            } catch (LineUnavailableException ex) {
              System.out.println("Got an exception trying to play a note: " + ex);
            }
          }
        }
        if(e.getSource().equals(clearS)){
            solPanel.notes.clear();
            for(MyNote note : cfPanel.notes) {
                note.setColor(Color.black);
                note.highlighted = false;
            }
            mess.removeAll();
            mess.repaint();
            cfPanel.repaint();
            solPanel.repaint();
        }
        if(e.getSource().equals(clearCF)){
            cfPanel.notes.clear();
            for(MyNote note : solPanel.notes) {
                note.setColor(Color.black);
                note.highlighted = false;
            }
            mess.removeAll();
            mess.repaint();
            solPanel.repaint();
            cfPanel.repaint();
        }
        if(e.getSource().equals(major)) {
            keyList.removeAllItems();
            for(int i = 0; i < majorKeys.length; i++)
                keyList.addItem(majorKeys[i]);
            keyList.setSelectedIndex(7);
        }
        if(e.getSource().equals(minor)) {
            keyList.removeAllItems();
            for(int i = 0; i < minorKeys.length; i++)
                keyList.addItem(minorKeys[i]);
            keyList.setSelectedIndex(7);
        }
        if(e.getSource().equals(checkCF)){
            checkCF();
        }
        if(e.getSource().equals(check)){
            Key key = new Key(new Pitch('c'));
            cfPitches = new Pitch[cfPanel.notes.size()];
            solPitches = new Pitch[solPanel.notes.size()];
            cfPanel.setPitches();
            solPanel.setPitches();
            for(int i = 0; i < cfPitches.length; i++) {
                MyNote note = cfPanel.notes.get(i);
                cfPitches[i] = key.getPitch(note.pitchName(), note.octave);
            }
            for(int i = 0; i < solPitches.length; i++) {
                MyNote note = solPanel.notes.get(i);
                solPitches[i] = key.getPitch(note.pitchName(), note.octave);
            }
            CantusFirmus CFtoCheck = new CantusFirmus(cfPitches, key);
            FirstSpecies FStoCheck = new FirstSpecies(solPitches, key);
            FStoCheck.addCantusFirmus(CFtoCheck);
            Collection<Comment> comments = FStoCheck.check();
            if(comments.size() == 0) {
                comments.add(new Comment("No errors found!", Comment.Severity.LGTM));
            }
            for(MyNote note : solPanel.notes) {
                note.setColor(Color.black);
                note.highlighted = false;
            }
            for(MyNote note : cfPanel.notes) {
                note.setColor(Color.black);
                note.highlighted = false;
            }
            for(Comment c : comments) {
                for(int i = 0; i < c.notes.length; i++) {
                    for(int j = 0; j < solPitches.length; j++) {
                        if( c.notes[i] == solPitches[j]) {
                            solPanel.notes.get(j).setColor(getColor(c));
                        }
                    }
                    for(int j = 0; j < cfPitches.length; j++) {
                        if( c.notes[i] == cfPitches[j]) {
                            cfPanel.notes.get(j).setColor(getColor(c));
                        }
                    }
                }
            }
            solPanel.repaint();
            cfPanel.repaint();
            mess.removeAll();
            mess.setMessages(comments);
            //mess.displayMessages();
        }
        if (e.getSource().equals(compose)) {
            if(!checkCF())
                return;
            if (solutions == null) {
                Key key = new Key(new Pitch('c'));
                Pitch[] cfPitches = new Pitch[cfPanel.notes.size()];
                cfPanel.setPitches();
                for(int i = 0; i < cfPitches.length; i++) {
                    MyNote note = cfPanel.notes.get(i);
                    cfPitches[i] = key.getPitch(note.pitchName(), note.octave);
                    //System.out.println(note);
                    //System.out.println(CantusFirmus[i]);
                }
                CantusFirmus CFtoComposeTo = new CantusFirmus(cfPitches, key);
                //Solver s = new Solver();
                //s.solveLater(CFtoComposeTo);
                solutions = CFGen.compose(CFtoComposeTo);
                //  System.out.println("Found " + solutions.size() + " possible solutions.");
            }
            for(MyNote note : solPanel.notes) {
                note.setColor(Color.black);
                note.highlighted = false;
            }
            for(MyNote note : cfPanel.notes) {
                note.setColor(Color.black);
                note.highlighted = false;
            }
            mess.removeAll();
            //System.out.println(chosen);
            String solString = null;
            
            
            while(solString == null) {
                synchronized (solutions) {
                    int chosen = (int) ( Math.random() * solutions.size() );
                    int ctr = 0;
                    for(String s : solutions) {
                        if( ctr++ == chosen ) {
                            solString = s;
                        }
                    }
                }
            }
            // String solString = solutions.toArray()[(int) chosen].toString();
            
            //System.out.println(solString);
            solPanel.displayString(solString);
            solPanel.repaint();
            cfPanel.repaint();
            mess.repaint();
        }
        if (e.getSource() == keyList) {
            String newKey = (String) keyList.getSelectedItem();
            //System.out.println(newKey);
        }
    }   
}